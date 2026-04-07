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
      drawerTitle: '',
      // 当前抽屉关联的菜单
      currentMenuId: null,
      currentMenuName: '',

      // 功能权限数据表格
      datatableFunc: {
        headers: [
          { title: '权限名称', value:'menuName'},
          { title: '请求地址', value:'url'},
          { title: '操作', value:'operation', align:"center"}
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
      doAjaxPost(this.url("/system/menu/list"), {parentId: menuId, menuType: 'F'}, (result) => {
        if (result.state) {
          this.datatableFunc.items = result.data;
        } else {
          this.toast(result.message, 'warning');
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
        this.mergeValue(this.funcFormData, selectedItems.length > 0? selectedItems[0] : {});
      }
    },

    // 关闭表单抽屉窗口
    closeWinFormDrawer() {
      this.winFormDrawer = false;
      this.resetForm('funcDataForm');
      this.openWinDrawer(this.currentMenuId, this.currentMenuName);
    },

    // 提交功能权限表单数据
    doFuncSubmit() {
      this.posting = true;
      let method = this.funcFormData.menuId? "update" : "save";
      doAjaxPost(this.url("/system/menu/func/" + method), this.funcFormData, (result) => {
        if (result.state) {
          this.toast("操作成功");
          this.closeWinFormDrawer();
        } else {
          this.toast(result.message, 'warning');
        }
      });
    },

    /*
     * 语言变更后的回调
     */
    changeLangCallback() {
      t(this.datatableFunc.headers);
    }
  }
};