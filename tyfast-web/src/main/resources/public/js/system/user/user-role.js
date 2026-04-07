/*
 * 用户角色授权的业务逻辑
 */
const grantMixin = {
  data: function() {
    return {
      selectedItemName: null,
      searchKey: '',

      // 抽屉窗口
      winDrawer: false,
      winDrawerWidth: 800,

      // 表单抽屉窗口
      winFormDrawer: false,

      // 已授予的角色数据表格
      datatableRole: {
        headers: [
          { title: '#', value:'index', align:"center", width:80},
          { title: '角色名称', value:'roleName', sortable:true},
          { title: '操作', value:'operation', align:"center"}
        ],
        items: []
      },

      // 可授予的角色数据表格
      datatableRoleCan: {
        headers: [
          { title: '#', value:'index', align:"center", width:80},
          { title: '角色名称', value:'roleName'}
        ],
        items: []
      },

      // 角色授权表单数据
      grantFormData: {
        userId: null,
        selectedItems: []
      }
    }
  },

  watch: {
    winDrawer: function(value) {
      if (!value) {
        this.loading = false;
        this.datatableRole.items = [];
      }
    },

    winFormDrawer: function(value) {
      if (value) {
        this.winDrawerWidth = this.winDrawerWidth * 1.2;
      } else {
        this.winDrawerWidth = 800;
      }
    }
  },

  methods: {
    // 打开抽屉窗口
    openWinDrawer(userId, userName, realName) {
      this.grantFormData.userId = userId;
      this.winDrawer = true;
      this.selectedItemName = this.showDrawerTitle(userName, realName);
      this.getGrantData();
    },

    // 关闭抽屉窗口
    closeWinDrawer() {
      this.winDrawer = false;
    },

    // 打开表单抽屉编辑窗口
    openWinFormDrawer() {
      this.winFormDrawer = true;

      // 获取可授予的角色数据
      doAjaxGet(this.url("/system/user/grant/can/list/" + this.grantFormData.userId), null, (result) => {
        if (result.state) {
          this.datatableRoleCan.items = result.data;
        } else {
          this.toast(result.message, 'warning');
        }
      });
    },

    // 关闭表单抽屉窗口
    closeWinFormDrawer() {
      this.winFormDrawer = false;
      this.loading = false;
      this.datatableRoleCan.items = [];
      this.grantFormData.selectedItems = [];
      this.getGrantData();
    },

    // 提交授权数据
    doGrantSubmit() {
      this.posting = true;
      let formData = {userId: this.grantFormData.userId};
      formData.ids = "";
      this.grantFormData.selectedItems.filter(id => {formData.ids += (id + ",")});
      formData.ids = formData.ids.clean();
      doAjax(this.url("/system/user/grant/save"), formData, (result) => {
        if (result.state) {
          this.toast("操作成功");
        } else {
          this.toast(result.message, 'warning');
        }
        this.closeWinFormDrawer();
      });
    },

    // 删除授予的角色数据
    doRoleDelete(roleId) {
      this.posting = true;
      doAjax(this.url("/system/user/grant/del/" + roleId), {userId: this.grantFormData.userId}, (result) => {
        if (result.state) {
          this.toast("操作成功");
        } else {
          this.toast(result.message, 'warning');
        }
        this.getGrantData();
      });
    },

    // 获取用户授权数据
    getGrantData() {
      this.loading = true;
      doAjaxGet(this.url("/system/user/grant/list/" + this.grantFormData.userId), null, (result) => {
        if (result.state) {
          this.datatableRole.items = result.data;
        } else {
          this.toast(result.message, 'warning');
        }
      });
    },

    // 语言变更后的回调
    changeLangCallback() {
      t(this.datatable.headers);
      t(this.datatableRole.headers);
      t(this.datatableRoleCan.headers);
      t(this.genderList);
    }
  }
}