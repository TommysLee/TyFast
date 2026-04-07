/*
 * 第三方应用配置业务逻辑
 */
const thirdConfigMixin = {
  data() {
    return {
      // 应用配置表单
      configFormData: {
        orgId: null,
        appKey: null,
        appSecret: null
      },

      // 第三方应用配置窗口
      configDialog: false,
      tab: 0
    }
  },

  methods: {
    /*
     * 打开配置窗口
     */
    openConfigDialog(orgId) {
      this.configDialog = true;
      this.configFormData.orgId = orgId;
      doAjaxGet(this.url("/system/thirdConfig/list/" + orgId), null, result => {
        if (result.state) {
          if (result.data && result.data.length > 0) {
            let ysconfig = result.data[0];
            this.configFormData.appKey = ysconfig.appKey;
            this.configFormData.appSecret = ysconfig.appSecret;
          }
        } else {
          this.toast(result.message, 'warning');
        }
      })
    },

    /*
     * 关闭配置窗口
     */
    closeConfigDialog() {
      this.configDialog = false;
      this.resetForm('configForm');
    },

    /*
     * 提交配置数据
     */
    doConfigSubmit() {
      this.posting = true;
      doAjax(this.url("/system/thirdConfig/save"), this.configFormData, (result) => {
        if (result.state) {
          this.closeConfigDialog();
          this.toast("操作成功");
        } else {
          this.toast(result.message, 'warning');
        }
      });
    }
  }
}