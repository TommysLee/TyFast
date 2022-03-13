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
            { text: '菜单名称'},
            { text: '排序', align:"center"},
            { text: '请求地址'},
            { text: '创建时间', align:"center"},
            { text: '操作', align:"center"}
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
      dialogTitle: null,
      // 子节点标记
      childFlag: false
    }
  },

  computed: {
    topMenus: function() {
      let m = this.datatable.items.filter(currentValue => {
        return !currentValue.disabled && currentValue.level == 1;
      });
      m.unshift({menuId: '0', menuName: '根目录'});
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
        let _this = this;
        this.loading = true;
        this.scrollTop();

        doAjax(ctx + "system/menu/list", {menuType: 'M'}, (data) => {
          if (data.state) {
            // 将列表数据包装为树结构数据
            _this.datatable.items = _this.wrapTreeData(data.data, 'menuId', _this.datatable.headers);

            // 展开所有节点
            this.$nextTick(() => {
              this.$refs.treeDatatable.updateAll(true);
            });
          } else {
            _this.toast(data.message, 'warning');
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
        let _this = this;
        doAjax(ctx + "system/menu/single/" + id, null, (data) => {
          if (data.state) {
            _this.copyValue(_this.formData, data.data);
          } else {
            _this.toast(data.message, 'warning');
          }
        }, "GET");
      }
    },

    /*
     * 关闭表单编辑画面
     */
    closeFormDialog() {
      this.winDialog = false;
      this.resetForm('dataForm', 'observer');

      // 设置默认值
      this.$nextTick(() => {
        this.formData.icon = 'mdi-view-dashboard';
        this.formData.orderNum = 1;
        this.formData.parentId = '0';
      });
    },

    /*
     * 提交表单数据
     */
    doSubmit() {
      this.posting = true;
      let _this = this;

      // 子菜单去掉图标
      if ('0' != this.formData.parentId) {
        this.formData.icon = '';
      }

      let method = this.formData.menuId? "update" : "save";
      doAjax(ctx + "system/menu/" + method, this.formData, (data) => {
        if (data.state) {
          _this.toast("操作成功");
          _this.closeFormDialog();
          _this.doQuery();
        } else {
          _this.toast(data.message, 'warning');
        }
      });
    },

    /*
     * 删除数据
     */
    doDelete(menuId, isFunc) {
      this.posting = true;
      let _this = this;
      doAjax(ctx + "system/menu/del/" + menuId, null, (data) => {
        _this.$refs[menuId].isActive=false;
        if (data.state) {
          _this.toast("删除成功");

          if (isFunc) { // 刷新功能权限列表
            _this.openWinDrawer(_this.currentMenuId, this.currentMenuName);
          } else { // 刷新菜单列表
            _this.doQuery();
          }
        } else {
          _this.toast(data.message, 'warning');
        }
      }, "GET");
    }
  }
};
