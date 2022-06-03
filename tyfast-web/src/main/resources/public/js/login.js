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
    // Ajax正在发送数据的标识
    posting: false,
    // 登录成功标识
    success: false
  },
  mounted() {
    this.$nextTick(() => {
      this.$refs.loginNameInput.focus();
    });
    localStorage.removeItem("navMenus");
  },
  methods: {
    doLogin() {
      let _this = this;
      this.posting = true;
      this.errorMessage = '';

      // RSA加密
      this.formData.password = rsaEncrypt(this.password);

      doAjax("login", this.formData, function(data) {
        _this.posting = false;

        // 登录成功
        if (data.state) {
          _this.success = true;
          window.location.href = ctx + "index";
        } else { // 登录失败
          _this.errorMessage = data.message;
        }
      }, "POST", (errMsg) => {
        _this.errorMessage = errMsg;
      });
    }
  }
});