/*
 * 字典项业务逻辑
 */
const itemsMixin = {
  data: function() {
    return {
      // 抽屉窗口
      winDrawer: false,
      winDrawerWidth: 800,
      // 表单抽屉窗口
      winFormDrawer: false,
      winFormDrawerWidth: 800,
      drawerTitle: null,
      // 当前抽屉关联的字典
      currentCode: null,
      currentName: null,

      // 字典项数据表格
      datatableItems: {
        headers: [],
        items: []
      },
      // 字典项表单数据
      formDataItem: {

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
    openWinDrawer(code, name) {
      this.currentCode = code;
      this.currentName = name;
      this.winDrawer = true;
    },

    // 关闭抽屉窗口
    closeWinDrawer() {
      this.winDrawer = false;
      this.datatableItems.items = [];
    },

    // 打开表单抽屉编辑窗口
    openWinFormDrawer(title) {
      this.winFormDrawer = true;
      this.drawerTitle = title;
    },

    // 关闭表单抽屉窗口
    closeWinFormDrawer() {
      this.winFormDrawer = false;
      this.resetForm('itemDataForm', 'itemObserver');
      this.openWinDrawer(this.currentCode, this.currentName);
    },

    // 提交字典项表单数据
    doItemSubmit() {
    }
  }
};