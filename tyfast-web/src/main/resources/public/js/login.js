// 初始化Vue
let app = new Vue({
  el: "#app",
  vuetify: new Vuetify(),
  data: {
    formData: {
      loginName: null,
      password: null
    },
    password: null,
    errorMessage: '',
    lang: 'zh_CN',
    // Ajax正在发送数据的标识
    posting: false,
    // 登录成功标识
    success: false
  },
  computed: {
    isCN() {
      let lang = $cookies.get('lang') || this.lang;
      return this.lang == lang;
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.$refs.loginNameInput.focus();
    });
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
    $t: i18n.t
  }
});