const defaultLocale = 'zh-CN';
const defaultFallbackLocale = 'zh-CN';

/*
 * Vuetify实例
 */
const vuetify = Vuetify.createVuetify({
  locale: {
    locale: defaultLocale
  },
  defaults: {
    VTextField: {
      color: 'primary',
      variant: 'outlined',
      density: 'compact'
    },
    VTextarea: {
      color: 'primary',
      variant: 'outlined',
      density: 'compact'
    },
    VSelect: {
      variant: 'outlined',
      density: 'compact'
    },
    VRadioGroup: {
      color: 'primary'
    },
    VCheckbox: {
      color: 'primary'
    },
    VSwitch: {
      color: 'success'
    },
    VTreeview: {
      activeColor: 'primary'
    },
    VPagination: {
      activeColor: 'primary'
    },
    VDatePicker: {
      color: 'primary'
    }
  }
});

/*
 * Vue3国际化插件：Vue-i18N
 */
const i18n = VueI18n.createI18n({
  locale: defaultLocale,                  // 设置语言环境
  fallbackLocale: defaultFallbackLocale,  // 预设的语言环境【当前语言环境没有要获取的值时，默认从这个语言环境查找（预设的语言环境·首选语言缺少翻译时要使用的语言）】
  messages: {},                           // 设置各本地化的语言包
  preserveDirectiveContent: true          // 解决翻译内容闪烁的问题(有过渡动画时，可复现此问题)
});

/*
 * Vue3表单验证插件：VeeValidate
 */
if ('undefined' !== typeof(VeeValidate)) {
  VeeValidate.configure({
    generateMessage: context => {
      let field = i18n.global.t(context.field);
      return i18n.global.t(`validations.${context.rule.name}`, [field, ...context.rule.params]);
    }
  });

  // 导入官方所有规则
  Object.keys(VeeValidateRules.all).forEach(rule => {
    VeeValidate.defineRule(rule, VeeValidateRules.all[rule]);
  });

  // 扩展规则：中文字符
  VeeValidate.defineRule('chinese', value => {
    const reg = /^([\u4E00-\u9FA5\uF900-\uFA2D，。？！、；：【】“”‘’'']+)$/;
    return reg.test(value);
  });

  // 扩展规则：通用的同步验证规则
  VeeValidate.defineRule('check', (value, [funcName]) => {
    return appInstance[funcName](value);
  });

  // 扩展规则：Ajax异步验证的规则
  VeeValidate.defineRule('async', async (value, [url, ref, paramName], context) => {
    // 清除当前的错误状态
    const fieldRef = appInstance.$refs[ref] || appInstance.$refs[context.name];
    fieldRef && fieldRef.setErrors([]);

    // 请求参数
    let param = {};
    param[paramName || context.name] = value;

    // 发送请求，并等待响应结果
    let result = {};
    await doAjaxPost(ctx + url, param, (data) => {
      result = data;
    });
    return result.state? true : result.message;
  })
}

/**
 * Vue3 APP基类：封装基础业务功能，以简化使用(基于 Vue extends 实现功能复用)
 */
const baseApp = {
  uses: function(app) { // 插件统一注册入口
    app.use(i18n).use(vuetify);
    Vue3Snackbar && app.use(Vue3Snackbar.SnackbarService);
    return app;
  },
  components: {},
  directives: {
    blank: { //自定义指令：当文本框或文本域的值为null时，将null转换为空字符串
      bind: function(el) {
        let textEl = null;
        let tagName = el.tagName.toUpperCase();
        switch (tagName) {
          case "INPUT":
          case "TEXTAREA":
            textEl = el;
        }
        textEl = textEl || el.querySelector("input[type]");
        textEl = textEl || el.querySelector("textarea");
        textEl && textEl.addEventListener('blur', function () {
          if (this.value === '') {
            this.dispatchEvent(new Event('input'))
          }
        });
      }
    }
  },
  data: function() {
    return {
      fullscreenIcon: "mdi-fullscreen",
      isLinkWS: true, // 是否建立WebSocket连接
      isReadAlarms: true, // 是否读取系统近期告警数据
      isReadMenu: true, // 是否读取系统菜单数据
      sysNavStatus: true, // 左侧导航栏状态：显示/隐藏
      sysRailStatus: false, // 左侧导航栏是否只以图标形式显示
      availableOrgs: [], // 可用的机构列表
      menuName: '', // 页面上显示的菜单名称
      navMenus: [], // 导航菜单数据
      deactiveMenu: false, // 不高亮显示导航菜单
      enableMenuInfer: false, // 是否启用Menu智能推断
      storageMenuKey: "navMenus", // 存储在LocalStorage中的菜单数据Key
      storageRailStatusKey: "railStatus", // 存储在LocalStorage中的导航菜单形态状态Key
      vtheme: 'light', // Vuetify主题
      storageThemeKey: 'vuetifyTheme', // 存储在LocalStorage中的主题数据Key
      storageAlarmKey: 'alarms', // 存储在LocalStorage中的告警数据Key
      storageSlientKey: 'slient', // 存储在LocalStorage中的静音Key
      alarmSilent: false, // 告警静音
      alarmMessages: [], // 告警消息数据
      alarmLevelItems: [],  // 告警级别集合
      colors: { // 全局色值
        1: "primary",
        2: "amber",
        3: "pink-lighten-1",
        4: "red-darken-4"
      },
      langList: [],// 语言列表
      lang: null,  // 当前语言环境
      loading: false, // 数据加载状态
      posting: false, // 请求状态
      overlay: false, // 全屏Loading
      tabActivity: true, // 浏览器选项卡激活状态
      socketState: 0, // WebSocket连接状态: 0=初次连接中，1=掉线，9=连接成功
      pagination: { // 分页对象
        page: 1,
        pageSize: 20,
        vp: 10
      },
      assistHeight: 20, // 辅助元素的总高度
      dictConfig: { // 数据字典配置
        'ALARM-LEVEL': 'alarmLevelItems'
      },
      placeholder: '--', // 占位符文本
    }
  },
  computed: {
    // 项目根路径
    ctx() {
      const base = ctx || '/';
      return base.slice(0, -1);
    },
    // 是否为Dark Mode
    vdark() {
      return this.$vuetify.theme.global.current.dark;
    },
    // 是否移动端（根据屏幕尺寸判定）
    isMobile() {
      return this.$vuetify.display.xs || this.$vuetify.display.sm
    },
    // No Data Text From Vuetify3
    noDataText() {
      return this.$vuetify.locale.t('$vuetify.noDataText');
    },
    // Loading Text From Vuetify3
    loadingText() {
      return this.$vuetify.locale.t('$vuetify.loading');
    },
    // 租户ID
    tenantId() {
      return $cookies.get("stenantId") || this.ptenantId;
    },
    // 主租户ID
    ptenantId() {
      return $cookies.get("tenantId") || document.querySelector("#header-tenantId").innerHTML.trim();
    },
    // 打开的菜单组
    openMenus() {
      let opened = [];
      if (!this.deactiveMenu) {
        for (let m of this.navMenus) {
          if (m.selected) {
            opened.push(m.menuId);
          }
        }
      }
      return opened;
    },
    // 告警状态
    alarmStatus() {
      return this.alarmMessages.length > 0;
    },
    // 告警级别
    alarmLevels() {
      t(this.alarmLevelItems);
      return toMap(this.alarmLevelItems);
    }
  },
  watch: {
    sysRailStatus(val) {
      localStorage.setItem(this.storageRailStatusKey, val);
    },
    lang(val) {
      $cookies.set("lang", val, '1y');
      this.$i18n.locale = val;
      this.$vuetify.locale.current = val;

      // 加载语言本地化资源包
      this.loadLangResources(this.changeLangCallback || (() => {this.datatable && (this.datatable.headers instanceof Array) && t(this.datatable.headers)}));
    },
    tabActivity(val) {
      if (val) {
        this.onActivityStarted && this.onActivityStarted();
      } else {
        this.onActivityStopped && this.onActivityStopped();
      }
    },
    alarmMessages(val) {
      val = val || [];
      if (val.length > 10) { // 只保留最近10条告警记录
        val.pop();
      }
      localStorage.setItem(this.storageAlarmKey, JSON.stringify(val));
    },
    alarmSilent(val) {
      localStorage.setItem(this.storageSlientKey, val);
    }
  },
  created() {
    // 移动端默认收起导航栏
    if (this.isMobile) {
      this.sysNavStatus = false;
    }

    // 注册全屏事件监听器
    this.fullScreenListener();

    // 加载系统导航菜单
    if (this.isReadMenu) {
      this.loadNavMenus();
    }

    // 切换Vuetify主题
    this.switchTheme();

    // 读取查询参数
    this.param = readQueryParam(this.menuName, this.param);
    this.pagination.page = (this.param && this.param.page) || this.pagination.page;
  },
  mounted() {
    // 建立WebSocket通讯通道
    if (this.isLinkWS) {
      connect();
    }

    // 加载告警数据
    if (this.isReadAlarms) {
      this.loadAlarms();
    }

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
        this.sysRailStatus = !this.sysRailStatus;
      } else {
        this.sysNavStatus = !this.sysNavStatus;
        this.sysRailStatus = false;
      }
    },

    // 滚动到顶部
    scrollTop() {
      if (!this.method) {
        if (this.vnode) {
          this.vnode.$el && this.vnode.$el.scrollTo({top: 0, left: 0, behavior: 'smooth'});
        } else {
          window.scrollTo({top: 0, behavior: 'smooth'});
        }
      }
    },

    // 滚动到数据表格顶部
    scrollDTableTop(ref) {
      if (!(this.method === 'update' || this.method === 'del')) {
        ref = ref || 'dataTable';
        this.$refs[ref] && this.$refs[ref].$el.querySelector(".v-table__wrapper").scroll(0,0);
        this.isMobile && this.scrollTop();
      }
      this.method = null;
    },

    // 返回上一页
    back() {
      window.history.back();
    },

    // URL函数：自动添加项目名称，并支持随机参数，解决静态缓存问题
    url(link, appendTail, tv) {
      if (link) {
        link = this.ctx + link;
        if (appendTail) {
          let t = tv || new Date().getTime();
          if (typeof(link) === 'string') {
            link += (link.lastIndexOf("?") > -1? '&' : '?') + '_t=' + t;
          }
        }
      }
      return link;
    },

    // 获取合适的分页页码
    getFitPage(pagination, datatable) {
      pagination = pagination || this.pagination;
      datatable = datatable || this.datatable;

      let page = pagination.page ?? 1;
      let items = datatable.items || [];
      if (page > 1 && this.method === 'del') {
        page = items.length <= 1? (page - 1) : page;
      }
      if (this.method === 'save') {
        page = 1;
      }
      return page;
    },

    // Toast
    toast(msg, msgType) {
      // 只在浏览器Tab页签被激活的情况下，才触发
      if (this.tabActivity) {
        msg = (undefined !== msg && null !== msg)? this.$t(msg + '') : msg;
        if (msg) {
          this.$snackbar.add({
            type: msgType || 'success',
            text: msg
          })
        }
      }
    },
    // Clear All Toast
    clearAllToast() {
      this.$snackbar && this.$snackbar.clear();
    },

    // 合并属性值：将 Source 的属性值 复制到 Target (只复制Target存在的属性)
    mergeValue(target, source) {
      Object.keys(source).filter(p => p in target).forEach(p => target[p] = source[p])
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

    // 重置表单
    resetForm(formRef) {
      formRef = formRef || 'dataForm';
      let formNode = this.$refs[formRef];
      if (formNode) {
        let formContainer = formNode.$el.querySelector('.v-card-text');
        formContainer && formContainer.scroll(0,0);
        formNode.$el.reset();
        setTimeout(() => {
          formNode.resetForm && formNode.resetForm();
        }, 50)
      }
    },

    // 注销登录
    logout() {
      sessionStorage.clear();
      localStorage.clear();
      this.vtheme && localStorage.setItem(this.storageThemeKey, this.vtheme);
      window.location.href = this.url("/logout");
    },

    // 打开个人设置
    openUserSetting() {
      window.location.href = this.url("/user/profile/view")
    },

    // 获取系统菜单数据
    loadNavMenus(callback) {
      // 状态
      let sysRailStatus = localStorage.getItem(this.storageRailStatusKey);
      if (sysRailStatus) {
        this.sysRailStatus = !("false" === sysRailStatus);
      }

      // 数据
      let navMenusJson = localStorage.getItem(this.storageMenuKey);
      if (navMenusJson) {
        this.navMenus = JSON.parse(navMenusJson);
        this.highlightNavMenuItem();
      } else {
        doAjax(this.url("/system/menu/user/list"), null, result => {
          if (result.state) {
            // 将菜单URL，添加项目路径前缀
            result.data = result.data || [];
            result.data.filter(item => {
              if (item.url) {
                item.url = item.url.startsWith("http")? item.url : this.url(item.url);
              }
            });

            // 构建菜单树
            this.navMenus = this.wrapTreeData(result.data, 'menuId', null, null, "selected");
            this.navMenus.unshift({menuId: "1",menuName: "首页",icon: "mdi-home",url: this.url("/index"),selected: true}); // 添加"首页"菜单项
            this.highlightNavMenuItem();
            localStorage.setItem(this.storageMenuKey, JSON.stringify(this.navMenus));

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

    // 高亮显示导航菜单项
    highlightNavMenuItem() {
      let url = location.pathname;
      let item = null, parentItem = null, urlMatchItem = null, urlMatchParentItem = null;
      for (let m of this.navMenus) {
        if (!m.children) { // 没有子菜单的情况
          if (m.selected) {
            item = m;
          }
          if (m.url === url) {
            urlMatchItem = m;
          }
        } else { // 有子菜单的情况
          for (let c of m.children) {
            if (c.selected) {
              item = c;
              parentItem = m;
            }
            if (c.url === url) {
              urlMatchItem = c;
              urlMatchParentItem = m;
            }
          }
        }
      }

      // 若没有菜单被选中，但存在URL匹配菜单，则高亮显示URL匹配菜单
      if (null == item && null != urlMatchItem) {
        urlMatchItem.selected = true;
        urlMatchParentItem && (urlMatchParentItem.selected = true);
        console.log('若没有菜单被选中，但存在URL匹配菜单，则高亮显示URL匹配菜单')
      } else if (null != item && null != urlMatchItem && item.url !== urlMatchItem.url) { // 若有菜单被选中，但与URL匹配菜单不同，则高亮显示URL匹配菜单
        item.selected = false;
        parentItem && (parentItem.selected = false);
        urlMatchItem.selected = true;
        urlMatchParentItem && (urlMatchParentItem.selected = true);
        console.log('若有菜单被选中，但与URL匹配菜单不同，则高亮显示URL匹配菜单')
      }

      // Menu推断功能
      if (null == urlMatchItem && this.enableMenuInfer) {
        this.inferSuitableMenu(item, parentItem);
      }
    },

    // Menu推断，高亮最合适的Menu
    inferSuitableMenu(menuItem, parentItem) {
      const url = location.pathname;
      let maxSimilarity = 0;
      if (menuItem) { // 优先与传递来的菜单比较
        let { similarity } = prefixTextSimilarity(url, menuItem.url);
        maxSimilarity = similarity;
      }

      // 从菜单集合中，逐一查找，找到相似度最高的菜单
      for (let m of this.navMenus) {
        m.selected = false;
        if (m.children) {
          for (let c of m.children) {
            let { similarity } = prefixTextSimilarity(url, c.url);
            if (similarity > maxSimilarity) {
              maxSimilarity = similarity;
              menuItem = c;
              parentItem = m;
            }
            c.selected = false;
          }
        }
      }

      // 将相似度最高的Menu，高亮显示
      console.log("相似度最高Menu：", maxSimilarity, menuItem)
      if (menuItem) {
        menuItem.selected = true;
      }
      if (parentItem) {
        parentItem.selected = true;
      }
    },

    // 重载系统菜单数据
    reloadNavMenus(callback) {
      localStorage.removeItem(this.storageMenuKey);
      this.loadNavMenus(callback);
    },

    // 打开目标页面
    gotoPage(item, e) {
      clearQueryParam();
      item = item || {};
      for (let m of this.navMenus) {
        m.selected = item.parentId === m.menuId;
        if (m.children) { // 子菜单
          for (let c of m.children) {
            c.selected = item.menuId === c.menuId;
          }
        }
      }
      localStorage.setItem(this.storageMenuKey, JSON.stringify(this.navMenus));

      // 跳转到目标页
      if ('_self' === (item.target??'_self')) {
        e.preventDefault();
        this.overlay = true;
        window.location.href = item.url;
      }
    },

    // 获取数据字典值
    doQueryDicts(config, callback) {
      config = config || this.dictConfig;
      let codes = Object.keys(config).join(",");
      if (codes.length > 0) {
        doAjaxSimple(this.url("/system/dict/items"), {codes}, (data) => {
          let dataMap = data.data;
          if (callback) {
            callback(dataMap || {});
          } else {
            if (dataMap) {
              for (let p in config) {
                let items = dataMap[p];
                if (items) {
                  this[config[p]] = items;
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
        val = val[0];
        this.vtheme = val;
        localStorage.setItem(this.storageThemeKey, val);
        this.themeEvent && this.themeEvent();
      } else {
        this.vtheme = localStorage.getItem(this.storageThemeKey) || 'dark';
      }
      this.$vuetify.theme.change(this.vtheme === 'dark' ? 'dark' : 'light');
    },

    // 加载语言列表
    loadLangList() {
      doAjaxGetSimple(this.url("/lang/list"), null, result => {
        this.langList = result.data || [];
      });

      // 设置当前语言环境
      this.lang = $cookies.get("lang") || defaultLocale;
    },

    // 加载语言本地化资源
    loadLangResources(callback) {
      let messages = i18n.global.messages[this.lang];
      if (!messages) { // 若不存在静态语言包，则加载
        loadJScript(this.url("/assets/lang/" + this.lang + ".js", true, _v), callback);
      } else {
        callback && callback();
      }
    },

    // 加载可用的机构数据
    loadAvailableOrgs() {
      doAjaxGetSimple(this.url("/system/org/visible/list"), null, (result) => {
        if (result.state && result.data) {
          this.availableOrgs = result.data;
        } else {
          this.toast("加载机构树失败！", 'warning');
        }
      })
    },

    // 加载告警数据
    loadAlarms() {
      let slient = localStorage.getItem(this.storageSlientKey);
      if (slient) {
        this.alarmSilent = !("false" === slient)
      }
      this.loadAlarmsFromLocal() || this.loadAlarmsFromRemote();
    },

    // 从本地加载告警消息数据
    loadAlarmsFromLocal() {
      let alarmsJson = localStorage.getItem(this.storageAlarmKey);
      if (alarmsJson) {
        return this.alarmMessages = JSON.parse(alarmsJson);
      }
      return null;
    },

    // 从远程加载告警消息数据
    loadAlarmsFromRemote() {
      this.alarmMessages = [];
    },

    // 查看告警详情
    viewAlarm(item) {
    },

    // 如果只为空，则显示默认值
    defaultIfBlank(val, defaultText = this.placeholder) {
      return null != val? val : defaultText;
    },

    // 空EChart图表提示函数
    emptyChartTips(chartRef) {
      this.$nextTick(() => {
        this.$refs[chartRef].chart.showLoading({text: this.noDataText, showSpinner:false, maskColor:'transparent', fontSize: 20, fontWeight:'bold'})
      })
    }
  }
};

/**
 * Vue3注册组件
 */
if ('undefined' !== typeof(VeeValidate)) {
  baseApp.components['tform'] = VeeValidate.Form;
  baseApp.components['tfield'] = VeeValidate.Field;
}
if ('undefined' !== typeof(Vue3Snackbar)) {
  baseApp.components['vue3-snackbar'] = Vue3Snackbar.Vue3Snackbar;
}
if ('undefined' !== typeof(VueECharts)) {
  baseApp.components['v-chart'] = VueECharts;
  baseApp.provide = function() {
    return {
      [VueECharts.THEME_KEY]: Vue.computed(() => this.vdark?'dark':'default'),
      [VueECharts.LOADING_OPTIONS_KEY]: Vue.computed(() => this.vdark?{maskColor:'rgba(0, 0, 0, 0.6)', textColor:'#FFF', text:this.loadingText}:{text:this.loadingText})
    }
  };
}

/**
 * 浏览器选项卡显隐监听事件
 */
document.addEventListener("visibilitychange", function () {
  appInstance && (appInstance.tabActivity = !(document.hidden === true));
});
window.addEventListener('pagehide', function (event) {
  appInstance && (appInstance.tabActivity = event.persisted? true : false);
});