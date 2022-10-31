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
    langList: [], // 语言列表
    posting: false, // Ajax正在发送数据的标识
    success: false // 登录成功标识
  },
  computed: {
    isCN() {
      return this.lang === 'zh_CN';
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.$refs.loginNameInput.focus();
    });
    localStorage.removeItem("navMenus");
    localStorage.removeItem("langList");

    // 加载语言列表
    this.loadLangList();
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
    loadLangList() {
      doAjaxGet(ctx + "lang/list", null, result => {
        this.langList = result.data || [];
      });
      // 设置当前语言环境
      this.lang = $cookies.get("lang") || this.lang;
    },
    switchLang(val) {
      $cookies.set("lang", val, '1y');
      window.location.reload();
    },
    $t: i18n.t
  }
});