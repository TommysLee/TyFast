// 初始化Vue
let app = new Vue({
  el: "#app",
  vuetify: new Vuetify(),
  data: {
    formData: {
      password: null,
      newPassword: null
    },
    confirmPassword: null
  },

  mounted() {
    this.inactiveNavMenuItem();
  },

  methods: {
    doSubmit() {
      this.posting = true;
      doAjax(ctx + "system/user/password/update", this.formData, data => {
        if (data.state) {
          this.toast("密码修改成功");
          this.resetForm('dataForm', 'observer');
        } else {
          this.toast(data.message, 'warning');
        }
      });
    }
  }
});