/**
 * 封装共用业务功能，简化使用(主要是基于 Vue 混入功能实现 :: VUE全局混用：实现功能复用)
 */
Vue.mixin({
  vuetify: new Vuetify(),
  data: function() {
    return {
      fullscreenIcon: "mdi-fullscreen",
      sysNavStatus: true, // 左侧导航栏状态：显示/隐藏
      sysNavariantStatus: false, // 左侧导航栏是否只以图标形式显示
      sysNavariantWidth: 70, // 图标形式时，左侧导航栏的宽度
      menuName: '', // 页面上显示的菜单名称
      navMenus: [], // 导航菜单数据
      storageMenuKey: "navMenus", // 存储在LocalStorage中的菜单数据Key
      vtheme: 'light', // Vuetify主题
      storageThemeKey: 'vuetifyTheme', // 存储在LocalStorage中的主题数据Key
      loading: false, // 数据加载状态
      posting: false, // 请求状态
      overlay: false, // 全屏Loading
      socketState: 0, // WebSocket连接状态: 0=初次连接中，1=掉线，9=连接成功
      noDataText: "暂无数据",
      dataLoadingText: "数据加载中...",
      pagination: { // 分页对象
        page: 1,
        pageSize: 20,
        totalPages: 0,
        vp: 10
      },
      ctx
    }
  },
  computed: {
    // 是否为首页
    isHome() {
      let flag = location.pathname === ctx + 'index';
      if (flag) {
        this.inactiveNavMenuItem();
      }
      return flag;
    }
  },
  created() {
    if (this._uid === 1) {
      // 加载系统导航菜单
      this.getNavMenus();

      // 建立WebSocket通讯通道
      connect();

      // 切换Vuetify主题
      this.switchTheme();
    }
  },
  methods: {
    // 浏览器全屏模式与普通模式切换
    toggleFullScreen() {
      screenfull.toggle();
      this.fullscreenIcon = "mdi-fullscreen-exit" === this.fullscreenIcon? "mdi-fullscreen" : "mdi-fullscreen-exit";
    },

    // 切换导航菜单的显示方式
    toggleSysMenu() {
      if (this.sysNavStatus) {
        this.sysNavariantStatus = !this.sysNavariantStatus;
      } else {
        this.sysNavStatus = !this.sysNavStatus;
        this.sysNavariantStatus = false;
      }
    },

    // 滚动到顶部
    scrollTop(duration) {
      this.$vuetify.goTo(0, {duration: duration || 0});
    },

    // 返回上一页
    back() {
      window.history.back();
    },

    // Toast
    toast(msg, msgType) {
      msg = (undefined != msg && null != msg)? (msg + '') : msg;
      let toastObj = this.$message.topRight().closeButtonContent('x');
      switch (msgType) {
        case 'warning':
          toastObj.warning(msg);
          break;
        case 'error':
          toastObj.error(msg);
          break;
        default:
          toastObj.success(msg);
      }
    },

    // Vee验证策略vchange: 当值变化时验证
    vchange(context) {
      return context.value != null? { on: ['change'] } : { on: ['blur'] };
    },

    // 将 Source 的属性值 复制到 Target (只复制Target存在的属性)
    copyValue(target, source) {
      for (let p in target) {
        target[p] = source[p];
      }
    },

    // 将列表数据包装为树结构数据 (data 与 idKey 参数为必选)
    wrapTreeData(data, idKey, headers, parentKey, activeKey) {
      let datax = [];
      if (data instanceof Array && data.length > 0) {
        parentKey = parentKey || 'parentId';

        // 为不改变原始数据，所以将数据复制一份
        for (let item of data) {
          if (activeKey) {
            item[activeKey] = false;
          }
          datax.push({...item});
        }

        // 通过map 与 filter 进行数据变换
        let childIds = {}; // 记录所有子节点ID
        datax = datax.map((currentValue, index, arr) => {
          // 查找当前节点的所有子节点
          let children = [];
          for (let item of arr) {
            if (currentValue[idKey] === item[parentKey]) { // 若主外键相同
              children.push(item);
              childIds[item[idKey]] = item[idKey];
            }
          }
          if (children.length > 0) {
            currentValue.children = children;
          }
          return currentValue;
        }).filter((currentValue) => {
          // 过滤掉所有子节点（因为子节点已被添加到 父节点的 children 属性，若保留，则造成数据重复，也就不是树数据了）
          let flag = !childIds[currentValue[idKey]];
          if (flag) {
            // 通过递归计算各个节点的实际层级
            // 计算层级，主要用于在 TreeView组件中，让列数据对齐
            let updateNodeLevel = function(val) {
              if (val.children) {
                for (let i in val.children) {
                  let item = val.children[i];
                  item.level = val.level + 1;
                  updateNodeLevel(item);
                }
              }
            };
            currentValue.level = 1;
            updateNodeLevel(currentValue);
          }
          return flag;
        });
      }

      // 添加树表格的表头
      if (headers) {
        datax.unshift(headers);
      }
      return datax;
    },

    // 关闭 mini Confrim提示框
    closeXsConfirm(refId) {
      let domEl = this.$refs[refId];
      domEl = domEl instanceof Array? domEl[0] : domEl;
      domEl.isActive=false;
    },

    // 重置表单
    resetForm(form, observer) {
      if (form) {
        this.$refs[form].reset();
      }

      if (observer) {
        this.$refs[observer].reset();
      }
    },

    // 注销登录
    logout() {
      localStorage.clear();
      this.vtheme && localStorage.setItem(this.storageThemeKey, this.vtheme);
      window.location.href = ctx + "logout";
    },

    // 打开个人设置
    openUserSetting() {
      window.location.href = ctx + "system/user/profile/view"
    },

    // 获取项目根目录
    basePath() {
      const base = ctx || '/';
      return base.substr(0, base.length - 1);
    },

    // 获取系统菜单数据
    getNavMenus() {
      let navMenusJson = localStorage.getItem(this.storageMenuKey);
      if (navMenusJson) {
        this.navMenus = JSON.parse(navMenusJson);
        this.recheckActiveNavMenu();
      } else {
        doAjax(ctx + "system/menu/user/list", null, data => {
          if (data.state) {
            // 将菜单URL，添加项目路径前缀
            data.data = data.data || [];
            data.data.filter(item => {
              if (item.url) {
                item.url = this.basePath() + item.url;
              }
            });

            // 构建菜单树
            this.navMenus = this.wrapTreeData(data.data, 'menuId', null, null, "selected");
            localStorage.setItem(this.storageMenuKey, JSON.stringify(this.navMenus));
            this.recheckActiveNavMenu();
          } else {
            this.toast('获取左侧导航菜单失败，请稍后重试!', 'warning');
          }
        });
      }
    },

    // 打开目标页面
    gotoPage(item, e) {
      this.activeNavMenuItem(item);

      // 跳转到目标页
      if ("_self" == item.target) {
        e.preventDefault();
        this.overlay = true;
        window.location.href = item.url;
      }
    },

    // 高亮显示当前点击的菜单
    activeNavMenuItem(item) {
      let parentItem = this.inactiveNavMenuItem(item);
      if (parentItem) {
        parentItem.selected = true;
      }

      // 将当前点击的菜单置为活动状态
      item.selected = true;
      localStorage.setItem(this.storageMenuKey, JSON.stringify(this.navMenus));
    },

    // 将菜单项全部置为非活动状态
    inactiveNavMenuItem(item) {
      let parentItem = null;
      for (let m of this.navMenus) {
        for (let c of m.children) {
          c.selected = false;
          parentItem = item && (item.menuId === c.menuId)? m : parentItem;
        }
        m.selected = false;
      }
      return parentItem;
    },

    // 进一步检查菜单高亮的正确性
    recheckActiveNavMenu() {
      let currentURL = location.pathname;
      let currentItem = null;
      for (let m of this.navMenus) {
        for (let c of m.children) {
          if (c.url === currentURL) {
            currentItem = c;
            break;
          }
        }
      }

      // 找到有效菜单项，执行高亮操作
      if (currentItem) {
        this.activeNavMenuItem(currentItem);
      }
    },

    // Vuetify主题切换
    switchTheme(val) {
      if (val) {
        localStorage.setItem(this.storageThemeKey, val);
      } else {
        this.vtheme = localStorage.getItem(this.storageThemeKey) || 'light';
      }
      this.$vuetify.theme.dark = this.vtheme === 'dark';
    }
  }
});
