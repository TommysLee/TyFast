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
        headers: [
          { text: '字典项名称', value:'text'},
          { text: '值', value:'value'},
          { text: '操作', value:'operation', align:"center"}
        ],
        items: []
      },
      // 字典项表单数据
      formDataItem: {
        text: null,
        value: null
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
    },
    "formDataItem.value": function(value){
      let val = parseInt(value);
      if (!isNaN(val)) {
        this.formDataItem.value = val;
      }
    }
  },

  methods: {
    // 打开抽屉窗口
    openWinDrawer(dict) {
      this.currentCode = dict.code;
      this.currentName = dict.name;
      this.datatableItems.items = dict.items || [];
      this.winDrawer = true;
    },

    // 关闭抽屉窗口
    closeWinDrawer() {
      this.winDrawer = false;
      this.datatableItems.items = [];
    },

    // 打开表单抽屉编辑窗口
    openWinFormDrawer(title, item, index) {
      this.winFormDrawer = true;
      this.drawerTitle = title;

      if (item) {
        this.isUpdateItem = true;
        this.itemIndex = index;
        this.copyValue(this.formDataItem, item);
      }
    },

    // 关闭表单抽屉窗口
    closeWinFormDrawer() {
      this.winFormDrawer = false;
      this.isUpdateItem = false;
      this.resetForm('itemDataForm', 'itemObserver');
    },

    // 提交字典项表单数据
    doItemSubmit() {
      let item = {...this.formDataItem};
      let items = [...this.datatableItems.items];
      let size = items.length;
      if (this.isUpdateItem) {
        items.splice(this.itemIndex, 1, item);
      } else {
        items.push(item);
      }

      // 发送数据
      this.posting = true;
      this.doPostData((data) => {
        if (data.state) {
          this.datatableItems.items.splice(0, size, ...items);
          this.closeWinFormDrawer();
        } else {
          this.toast(data.message, 'warning');
        }
      }, items);
    },

    // 删除字典项数据
    doDeleteItem(index) {
      let items = [...this.datatableItems.items];
      this.doPostData(() => {
        this.datatableItems.items.remove(index);
      }, items.remove(index))
    },

    // 检查项值唯一性
    checkItemValue(value) {
      for (let index in this.datatableItems.items) {
        let item = this.datatableItems.items[index];
        if (item.value == value) {
          if (this.isUpdateItem && this.itemIndex == index) {
            continue;
          }
          return "值已存在，请更改"
        }
      }
      return true;
    },

    // 发送数据
    doPostData(callback, items) {
      doAjaxPost(ctx + "system/dict/item/merge", {code: this.currentCode, items: JSON.stringify(items)}, callback || function() {});
    }
  }
};