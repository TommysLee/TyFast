/*
 * 菜单授权+功能权限授权的业务逻辑
 */
const grantMixin = {
  data: function() {
    return {
      tab: 0,
      selectedItemName: null,

      // 抽屉窗口
      winDrawer: false,

      // 菜单与功能权限集合数据
      menuList: [],
      menuIdList: [],
      funcList: [],

      // 授权表单数据
      grantFormData: {
        roleId: null,
        menuList: [],
        funcList: []
      }
    }
  },

  watch: {
    winDrawer: function() {
      this.tab = 0;
      this.menuIdList = [];
      this.grantFormData.menuList = [];
      this.grantFormData.funcList = [];
    },
    tab(val) {
      if (1 === val) {
        this.$nextTick(() => {
          this.grantFormData.funcList = [...this.grantFormData.funcList]
        })
      }
    }
  },

  methods: {
    // 打开抽屉窗口
    openWinDrawer(roleId, roleName) {
      this.grantFormData.roleId = roleId;
      this.selectedItemName = roleName;
      this.winDrawer = true;
      this.loading = true;
      doAjax(this.url("/system/menu/list"), null, (result) => {
        if (result.state) {
          if (result.data) {
            // 菜单Tree
            this.menuList = this.wrapTreeData(result.data.filter(currentValue => {
              let flag = currentValue.menuType == 'M';
              if (flag) {
                this.menuIdList.push(currentValue.menuId);
              }
              return flag;
            }), 'menuId');

            // 权限Tree
            this.funcList = this.wrapTreeData(result.data.filter(currentValue => {
              return !(currentValue.parentId == "0");
            }), 'menuId').filter(v => {
              return v.children;
            });

            // 获取角色的已授权信息
            this.getGrantData(roleId);
          }
        } else {
          this.toast(result.message, 'warning');
        }
      });
    },

    // 关闭抽屉窗口
    closeWinDrawer() {
      this.winDrawer = false;
      this.loading = false;
    },

    // 获取当前角色的已授权信息
    getGrantData(roleId) {
      this.loading = true;
      doAjaxGet(this.url("/system/role/grant/list/" + roleId), null, result => {
        if (result.data) {
          let menuIdList = [];
          let funcList = [];
          result.data.filter(currentValue => {
            let menuId = currentValue.menuId;
            if (this.menuIdList.includes(menuId)) {
              menuIdList.push(menuId);
            } else {
              funcList.push(menuId);
            }
          });

          // 在树组件渲染好后，再初始化业务数据，确保正常显示
          this.$nextTick(() => {
            this.grantFormData.menuList = menuIdList;
            this.grantFormData.funcList = funcList;
          })
        }
      });
    },

    // 提交授权数据
    doGrantSubmit() {
      this.posting = true;

      // 拼装数据
      let grantData = [];
      this.grantFormData.menuList.concat(this.grantFormData.funcList).filter(currentValue => {
        grantData.push({menuId: currentValue});
      });

      // 发送数据
      doAjax(this.url("/system/role/grant/save"), {roleMenuJsonArray: JSON.stringify(grantData), roleId: this.grantFormData.roleId}, result => {
        if (result.state) {
          this.toast("操作成功");
          this.closeWinDrawer();
        } else {
          this.toast(result.message, 'warning');
        }
      });
    }
  }
}