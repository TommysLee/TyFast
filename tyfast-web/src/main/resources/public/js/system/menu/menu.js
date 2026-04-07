/*
 * 菜单的业务逻辑
 */
const menuMixin = {
  data: function() {
    return {
      menuName: "菜单管理",
      // 数据表格
      datatable: {
        headers: {
          disabled: true,
          headers: [
            { title: '菜单名称'},
            { title: '排序', align:"center"},
            { title: '请求地址'},
            { title: '创建时间', align:"center"},
            { title: '操作', align:"center"}
          ]
        },
        items: []
      },
      // 表单数据
      formData: {
        menuId: null,
        parentId: '0',
        menuName: null,
        icon: 'mdi-view-dashboard',
        url: null,
        target: '_self',
        orderNum: 1,
        remark: null
      },
      // 模态窗口
      winDialog: false,
      dialogTitle: '',
      // 子节点标记
      childFlag: false
    }
  },

  computed: {
    topMenus: function() {
      let m = this.datatable.items.filter(currentValue => {
        return !currentValue.disabled && currentValue.level == 1;
      });
      m.unshift({menuId: '0', menuName: this.$t('根目录')});
      return m;
    }
  },

  watch: {
    'formData.parentId': function(value) {
      this.childFlag = !('0' == value);
    }
  },

  /*
   * 加载列表数据
   */
  created() {
    this.datatable.items.push(this.datatable.headers);
    this.doQuery();
  },

  methods: {
    /*
     * 执行条件查询
     */
    doQuery() {
      if (!this.loading) {
        this.loading = true;
        this.scrollTop();

        doAjax(this.url("/system/menu/list"), {menuType: 'M'}, (result) => {
          if (result.state) {
            // 将列表数据包装为树结构数据
            this.datatable.items = this.wrapTreeData(result.data, 'menuId', this.datatable.headers);
          } else {
            this.toast(result.message, 'warning');
          }
        });
      }
    },

    /*
     * 打开表单编辑画面
     */
    openFormDialog(title, id) {
      this.formData.menuId = id || null;
      this.dialogTitle = title;
      this.winDialog = true;

      // 查询记录详情
      if (id) {
        this.posting = true;
        doAjaxGet(this.url("/system/menu/single/" + id), null, (result) => {
          if (result.state) {
            this.mergeValue(this.formData, result.data);
          } else {
            this.toast(result.message, 'warning');
          }
        });
      }
    },

    /*
     * 关闭表单编辑画面
     */
    closeFormDialog() {
      this.winDialog = false;
      this.resetForm();

      // 设置默认值
      this.$nextTick(() => {
        this.formData.icon = 'mdi-view-dashboard';
        this.formData.orderNum = 1;
        this.formData.parentId = '0';
        this.formData.target = '_self';
      });
    },

    /*
     * 提交表单数据
     */
    doSubmit() {
      this.posting = true;

      // 子菜单去掉图标
      if ('0' != this.formData.parentId) {
        this.formData.icon = '';
      }

      let method = this.formData.menuId? "update" : "save";
      doAjaxPost(this.url("/system/menu/" + method), this.formData, (result) => {
        if (result.state) {
          this.toast("操作成功");
          this.closeFormDialog();
          this.doQuery();
        } else {
          this.toast(result.message, 'warning');
        }
      });
    },

    /*
     * 删除数据
     */
    doDelete(item) {
      let menuId = item.menuId;
      doAjaxGet(this.url("/system/menu/del/" + menuId), null, (result) => {
        if (result.state) {
          this.toast("操作成功");

          if ("F" == item.menuType) { // 刷新功能权限列表
            this.openWinDrawer(this.currentMenuId, this.currentMenuName);
          } else { // 刷新菜单列表
            this.doQuery();
          }
        } else {
          this.toast(result.message, 'warning');
        }
      });
    }
  }
};
