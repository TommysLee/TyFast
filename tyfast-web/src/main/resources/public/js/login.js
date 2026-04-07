// 初始化Vue
const app = Vue.createApp({
  extends: baseApp,
  data() {
    return {
      formData: {
        loginName: null,
        password: null
      },
      password: null,
      errorMessage: '',
      success: false, // 登录成功标识
      isLinkWS: false, // 是否建立WebSocket连接
      isReadAlarms: false, // 是否读取系统近期告警数据
      isReadMenu: false, // 是否读取系统菜单数据
      dictConfig: {}
    }
  },
  computed: {
    isCN() {
      return this.lang === defaultLocale;
    }
  },
  mounted() {
    localStorage.removeItem("navMenus");
    localStorage.removeItem("langList");
  },
  methods: {
    doLogin() {
      this.posting = true;
      this.errorMessage = '';

      // RSA加密
      this.formData.password = rsaEncrypt(this.password);

      doAjax("login", this.formData, (data) => {
        this.posting = false;

        // 登录成功
        if (data.state) {
          this.success = true;
          window.location.href = ctx + "index";
        } else { // 登录失败
          this.errorMessage = data.message;
        }
      }, "POST", (errMsg) => {
        this.errorMessage = errMsg;
      });
    },
    forwardWx() {
      window.location.href = this.url("/open/oauth2/weixin/connect");
    },
    switchLang(val) {
      $cookies.set("lang", val, '1y');
      window.location.reload();
    },
    switchTheme(val) {
    }
  }
});
const appInstance = baseApp.uses(app).mount('#app');
