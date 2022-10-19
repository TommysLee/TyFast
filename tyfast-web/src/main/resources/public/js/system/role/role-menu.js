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
    }
  },

  methods: {
    // 打开抽屉窗口
    openWinDrawer(roleId, roleName) {
      this.grantFormData.roleId = roleId;
      this.winDrawer = true;
      this.selectedItemName = roleName;
      this.loading = true;
      let _this = this;
      doAjax(ctx + "system/menu/list", null, (data) => {
        if (data.state) {
          if (data.data) {
            // 菜单Tree
            _this.menuList = _this.wrapTreeData(data.data.filter(currentValue => {
              let flag = currentValue.menuType == 'M';
              if (flag) {
                this.menuIdList.push(currentValue.menuId);
              }
              return flag;
            }), 'menuId');

            // 权限Tree
            _this.funcList = _this.wrapTreeData(data.data.filter(currentValue => {
              return !(currentValue.parentId == "0");
            }), 'menuId').filter(v => {
              return v.children;
            });

            // 展开两颗树的所有节点
            _this.$nextTick(() => {
              _this.$refs.menuListTreeRef.updateAll(true);
            });

            // 获取角色的已授权信息
            _this.getGrantData(roleId);
          }
        } else {
          _this.toast(data.message, 'warning');
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
      doAjax(ctx + "system/role/grant/list/" + roleId, null, data => {
        if (data.data) {
          data.data.filter(currentValue => {
            let menuId = currentValue.menuId;
            if (this.menuIdList.includes(menuId)) {
              this.grantFormData.menuList.push(menuId);
            } else {
              this.grantFormData.funcList.push(menuId);
            }
          });
        }
      }, "GET");
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
      doAjax(ctx + "system/role/grant/save", {roleMenuJsonArray: JSON.stringify(grantData), roleId: this.grantFormData.roleId}, data => {
        if (data.state) {
          this.toast(this.$t("操作成功"));
          this.closeWinDrawer();
        } else {
          this.toast(data.message, 'warning');
        }
      });
    }
  }
}