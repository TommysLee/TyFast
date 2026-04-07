/*
 * 用户关联机构的业务逻辑
 */
const relatedMixin = {
  data() {
    return {
      // 抽屉窗口
      winRelatedDrawer: false,

      // 表单数据
      relatedForm: {
        userId: null,
        ids: []
      }
    }
  },

  watch: {
    winRelatedDrawer(value) {
      if (!value) {
        this.loading = false;
        this.relatedForm.ids = [];
      }
    }
  },

  computed: {
    orgTreeData() {
      return this.wrapTreeData(this.availableOrgs, 'orgId')
    }
  },

  methods: {
    // 打开抽屉窗口
    openWinRelatedDrawer(userId, userName, realName) {
      this.winRelatedDrawer = true;
      this.relatedForm.userId = userId;
      this.selectedItemName = this.showDrawerTitle(userName, realName);
      this.getRelatedData();
    },

    // 关闭抽屉窗口
    closeWinRelatedDrawer() {
      this.winRelatedDrawer = false;
    },

    // 获取用户关联的机构列表
    getRelatedData() {
      this.loading = true;
      doAjaxGet(this.url("/system/uorg/list/" + this.relatedForm.userId), null, result => {
          if (result.state) {
            let data = result.data || [];
            for (let uorg of data) {
              this.relatedForm.ids.push(uorg.orgId);
            }
          } else {
            this.toast(result.message, 'warning');
          }
      })
    },

    // 保存用户关联的机构
    doRelatedSubmit() {
      this.posting = true;
      doAjaxPost(this.url("/system/uorg/save/" + this.relatedForm.userId), {ids:this.relatedForm.ids}, (result) => {
        if (result.state) {
          this.toast("操作成功");
        } else {
          this.toast(result.message, 'warning');
        }
        this.closeWinRelatedDrawer();
      });
    }
  }
}