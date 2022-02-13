/*
 * 功能权限的业务逻辑
 */
const funcMixin = {
  data: function() {
    return {
      // 抽屉窗口
      winDrawer: false,
      winDrawerWidth: 800,
      // 表单抽屉窗口
      winFormDrawer: false,
      winFormDrawerWidth: 800,
      drawerTitle: null,
      // 当前抽屉关联的菜单
      currentMenuId: null,
      currentMenuName: null,

      // 功能权限数据表格
      datatableFunc: {
        headers: [
          { text: '权限名称', value:'menuName'},
          { text: '请求地址', value:'url'},
          { text: '操作', value:'operation', align:"center"}
        ],
        items: []
      },

      // 功能权限表单数据
      funcFormData: {
        menuId: null,
        parentId: null,
        menuName: null,
        url: null
      }
    }
  },

  watch: {
    winFormDrawer: function(value) {
      if (value) {
        this.winDrawerWidth = this.winDrawerWidth * 1.2;
      } else {
        this.winDrawerWidth = this.winFormDrawerWidth;
      }
    }
  },

  methods: {
    // 打开抽屉窗口
    openWinDrawer(menuId, menuName) {
      this.currentMenuId = menuId;
      this.currentMenuName = menuName;
      this.winDrawer = true;
      this.loading = true;
      let _this = this;
      doAjax(ctx + "system/menu/list", {parentId: menuId, menuType: 'F'}, (data) => {
        if (data.state) {
          _this.datatableFunc.items = data.data;
        } else {
          _this.toast(data.message, 'warning');
        }
      });
    },

    // 关闭抽屉窗口
    closeWinDrawer() {
      this.winDrawer = false;
      this.datatableFunc.items = [];
    },

    // 打开表单抽屉编辑窗口
    openWinFormDrawer(title, id) {
      this.winFormDrawer = true;
      this.drawerTitle = title;
      this.funcFormData.menuId = id || null;
      this.funcFormData.parentId = this.currentMenuId;

      // 从列表中获取待修改的数据明细
      if (id) {
        let selectedItems = this.datatableFunc.items.filter(currentValue => {
          return currentValue.menuId == id;
        });
        this.copyValue(this.funcFormData, selectedItems.length > 0? selectedItems[0] : {});
      }
    },

    // 关闭表单抽屉窗口
    closeWinFormDrawer() {
      this.winFormDrawer = false;
      this.resetForm('funcDataForm', 'funcObserver');
      this.openWinDrawer(this.currentMenuId, this.currentMenuName);
    },

    // 提交功能权限表单数据
    doFuncSubmit() {
      this.posting = true;
      let _this = this;

      let method = this.funcFormData.menuId? "update" : "save";
      doAjax(ctx + "system/menu/func/" + method, this.funcFormData, (data) => {
        if (data.state) {
          _this.toast("操作成功");
          _this.closeWinFormDrawer();
        } else {
          _this.toast(data.message, 'warning');
        }
      });
    }
  }
};