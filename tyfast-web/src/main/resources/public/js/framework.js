const defaultLang = 'zh_CN';
const vuetify = new Vuetify();

/**
 * 引入Toast插件
 */
VuetifyMessageSnackbar.setVuetifyInstance(vuetify);
Vue.prototype.$message = VuetifyMessageSnackbar.Notify;

/*
 * 配置表单验证插件 VeeValidate i18N 国际化
 */
VeeValidate.configure({
  defaultMessage: function(field, values) {
    values._field_ = i18n.t(field);
    return i18n.t(`validations.messages.${values._rule_}`, values);
  }
});

/*
 * 扩展VeeValidate验证规则
 */
// 中文字符规则
VeeValidate.extend('chinese', {
  validate: value => {
    const reg = /^([\u4E00-\u9FA5\uF900-\uFA2D，。？！、；：【】“”‘’'']+)$/;
    return reg.test(value);
  }
});

// 仅包含字母、数字、下划线、破折号等规则
VeeValidate.extend('letter_dash', {
  validate: value => {
    const reg = /^([A-Za-z0-9_/\-]+)$/;
    return reg.test(value);
  }
});

// 同步验证的规则
VeeValidate.extend('check', {
  validate: (value, {func}) => {
    return app[func](value);
  },
  params: ['func']
});

// Ajax异步验证的规则
VeeValidate.extend('async', {
  validate: async (value, {url, prop, ref}) => {
    // 请求参数
    let param = {};
    param[prop] = value;

    // 清除当前的错误状态
    if (app.$refs[ref + 'VP']) {
      app.$refs[ref + 'VP'].errors=[];
    }
    app.$refs[ref].loading = true;

    // 发送请求，并等待响应结果
    let result = {};
    await doAjaxPost(ctx + url, param, (data) => {
      result = data;
    });
    app.$refs[ref].loading = false;
    return result.state? true : result.message;
  },
  params: ['url', 'prop', 'ref']
});

/**
 * VueI18n 插件
 * 官方文档：https://kazupon.github.io/vue-i18n/zh/started.html
 */
const i18n = new VueI18n({
  locale: defaultLang,          // 设置语言环境
  fallbackLocale: defaultLang,  // 预设的语言环境【当前语言环境没有要获取的值时，默认从这个语言环境查找（预设的语言环境·首选语言缺少翻译时要使用的语言）】
  messages: {},                 // 设置各本地化的语言包
  silentFallbackWarn: true,     // 是否在回退到 fallbackLocale 或 root 时取消警告（如果为 true，则仅在根本没有可用的转换时生成警告，而不是在回退时。）
  silentTranslationWarn: true,   // 是否取消本地化失败时输出的警告
  preserveDirectiveContent: true // 解决翻译内容闪烁的问题(有过渡动画时，可复现此问题)
});

/**
 * 封装基础业务功能，以简化使用(主要是基于 Vue 混入功能实现 :: VUE局部混入：实现功能复用)
 */
const mixins =[{
  i18n,
  vuetify,
  data: function() {
    return {
      fullscreenIcon: "mdi-fullscreen",
      sysNavStatus: true, // 左侧导航栏状态：显示/隐藏
      sysNavariantStatus: false, // 左侧导航栏是否只以图标形式显示
      sysNavariantWidth: 70, // 图标形式时，左侧导航栏的宽度
      menuName: '', // 页面上显示的菜单名称
      navMenus: [], // 导航菜单数据
      storageMenuKey: "navMenus", // 存储在LocalStorage中的菜单数据Key
      storageNavStatusKey: "navStatus", // 存储在LocalStorage中的导航菜单状态Key
      vtheme: 'light', // Vuetify主题
      storageThemeKey: 'vuetifyTheme', // 存储在LocalStorage中的主题数据Key
      storageLangResKey: 'langResources', // 存储在LocalStorage中的语言本地化资源Key
      langList: [],// 语言列表
      lang: null,  // 当前语言环境
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
      dictConfig: {}, // 数据字典配置
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
  watch: {
    sysNavariantStatus(val) {
      localStorage.setItem(this.storageNavStatusKey, val);
    },
    lang(val) {
      let cookieLang = $cookies.get("lang");
      if (!cookieLang || cookieLang !== val) { // Cookie不存在 或 值不相等时，说明语言更改，则清除动态语言包
        localStorage.removeItem(this.storageLangResKey);
      }
      $cookies.set("lang", val, '1y');
      this.$i18n.locale = val;

      // 加载语言本地化资源包
      this.loadLangResources();
    }
  },
  created() {
    // 全屏事件监听器
    this.fullScreenListener();

    // 加载系统导航菜单
    this.getNavMenus();

    // 建立WebSocket通讯通道
    connect();

    // 切换Vuetify主题
    this.switchTheme();

    // 读取查询参数
    this.param = readQueryParam(this.menuName, this.param);
  },
  mounted() {
    // 获取数据字典
    this.doQueryDicts();

    // 加载语言列表
    this.loadLangList();
  },
  methods: {
    // 浏览器全屏事件监听
    fullScreenListener() {
      screenfull && screenfull.onchange(() => {
        this.fullscreenIcon = screenfull.isFullscreen? "mdi-fullscreen-exit" : "mdi-fullscreen";
      });
    },

    // 浏览器全屏模式与普通模式切换
    toggleFullScreen() {
      screenfull.toggle();
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
      if (this.$vnode) {
        this.$el && this.$el.scroll(0,0);
      } else {
        this.$vuetify.goTo(0, {duration: duration || 0});
      }
    },

    // 滚动到数据表格顶部
    scrollDTableTop(ref) {
      this.$refs[ref] && this.$refs[ref].$el.querySelector(".v-data-table__wrapper").scroll(0,0);
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
      form = form || 'dataForm';
      observer = observer || 'observer';

      if (this.$refs[form]) {
        this.$refs[form].reset();
      }

      if (this.$refs[observer]) {
        this.$refs[observer].reset();
      }
    },

    // 注销登录
    logout() {
      sessionStorage.clear();
      localStorage.clear();
      this.vtheme && localStorage.setItem(this.storageThemeKey, this.vtheme);
      window.location.href = ctx + "logout";
    },

    // 打开个人设置
    openUserSetting() {
      window.location.href = ctx + "user/profile/view"
    },

    // 获取项目根目录
    basePath() {
      const base = ctx || '/';
      return base.substr(0, base.length - 1);
    },

    // 获取系统菜单数据
    getNavMenus(callback) {
      // 状态
      let sysNavariantStatus = localStorage.getItem(this.storageNavStatusKey);
      if (sysNavariantStatus) {
        this.sysNavariantStatus = !("false" === sysNavariantStatus);
      }

      // 数据
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

            // 回调函数
            if (callback) {
              callback(this.navMenus);
            }
          } else {
            this.toast('获取左侧导航菜单失败，请稍后重试!', 'warning');
          }
        });
      }
    },

    // 重载系统菜单数据
    reloadNavMenus(callback) {
      localStorage.removeItem(this.storageMenuKey);
      this.getNavMenus(callback);
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

    // 获取数据字典值
    doQueryDicts(callback, config) {
      let codes = Object.keys(config || this.dictConfig).join(",");
      if (codes.length > 0) {
        doAjaxPost(ctx + "system/dict/items", {codes}, (data) => {
          let dataMap = data.data;
          if (callback) {
            callback(dataMap || {});
          } else {
            if (dataMap) {
              for (let p in this.dictConfig) {
                let items = dataMap[p];
                if (items) {
                  this[this.dictConfig[p]] = items;
                }
              }
            }
          }
        });
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
      this.$nextTick(() => {
        this.themeEvent && this.themeEvent();
        let bodyClasslist = document.querySelector("body").classList;
        if (this.$vuetify.theme.dark) {
          bodyClasslist.add('theme--dark');
        } else {
          bodyClasslist.remove('theme--dark');
        }
      });
    },

    // 加载语言列表
    loadLangList() {
      doAjaxGet(ctx + "lang/list", null, result => {
        this.langList = result.data || [];
      });

      // 设置当前语言环境
      this.lang = $cookies.get("lang") || defaultLang;
    },

    // 加载语言本地化资源
    loadLangResources() {
      let messages = i18n.messages[this.lang];
      if (!messages) { // 若不存在静态语言包，则加载
        loadJScript(ctx + "assets/lang/" + this.lang + ".js?v=" + _v);
      }

      if (!messages || !messages['websiteName']) { // 若不存在动态语言包，则加载
        let langResJson = localStorage.getItem(this.storageLangResKey);
        if (typeof(langResJson) === 'string') { // 从缓存加载
          loadLocaleMessages(this.lang, JSON.parse(langResJson || '{}'))
        } else { // 从服务器加载
          let lang = this.lang;
          doAjaxGet(ctx + 'lang/resources?v=' + _v, null, result => {
            if (this.lang === lang && result.data) {
              loadLocaleMessages(this.lang, result.data);
              // 缓存动态语言包
              localStorage.setItem(this.storageLangResKey, JSON.stringify(result.data));
            }
          });
        }
      }
    }
  }
}]
