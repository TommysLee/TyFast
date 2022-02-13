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
          { text: '序号', value:'index', align:"center", width:80},
          { text: '角色名称', value:'roleName'},
          { text: '操作', value:'operation', align:"center", sortable:false}
        ],
        items: []
      },

      // 可授予的角色数据表格
      datatableRoleCan: {
        headers: [
          { text: '序号', value:'index', align:"center", width:80, filterable:false},
          { text: '角色名称', value:'roleName'}
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
      this.selectedItemName = userName + (realName? (' (' + realName + ')') : '');
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
      doAjax(ctx + "system/user/grant/can/list/" + this.grantFormData.userId, null, (data) => {
        if (data.state) {
          this.datatableRoleCan.items = addIndexPropForArray(data.data);
        } else {
          this.toast(data.message, 'warning');
        }
      }, "GET");
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
      this.grantFormData.selectedItems.filter(role => {formData.ids += (role.roleId + ",")});
      formData.ids = formData.ids.clean();
      doAjax(ctx + "system/user/grant/save", formData, (data) => {
        if (data.state) {
          this.toast("操作成功");
        } else {
          this.toast(data.message, 'warning');
        }
        this.closeWinFormDrawer();
      });
    },

    // 删除授予的角色数据
    doRoleDelete(roleId) {
      this.posting = true;
      doAjax(ctx + "system/user/grant/del/" + roleId, {userId: this.grantFormData.userId}, (data) => {
        if (data.state) {
          this.toast("操作成功");
        } else {
          this.toast(data.message, 'warning');
        }
        this.getGrantData();
      });
    },

    // 获取用户授权数据
    getGrantData() {
      this.loading = true;
      doAjax(ctx + "system/user/grant/list/" + this.grantFormData.userId, null, (data) => {
        if (data.state) {
          this.datatableRole.items = addIndexPropForArray(data.data);
        } else {
          this.toast(data.message, 'warning');
        }
      }, "GET");
    }
  }
}