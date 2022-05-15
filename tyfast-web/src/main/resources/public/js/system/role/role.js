/*
 * 角色的业务逻辑
 */
const roleMixin = {
  data: function () {
    return {
      menuName: "角色管理",
      // 查询条件
      param: {
        roleName: null
      },
      // 数据表格
      datatable: {
        headers: [
          { text: '序号', value:'index', align:"center"},
          { text: '角色名称', value:'roleName'},
          { text: '备注', value:'remark'},
          { text: '创建时间', value:'createTime', align:"center", width:180},
          { text: '操作', value:'operation', align:"center"}
        ],
        items: []
      },
      // 表单数据
      formData: {
        roleId: null,
        roleName: null,
        remark: null
      },
      // 模态窗口
      winDialog: false,
      dialogTitle: null
    }
  },

  /*
   * 加载列表数据
   */
  created() {
    this.doQuery();
  },

  methods: {
    /*
     * 执行条件查询
     */
    doQuery(page) {
      if (!this.loading) {
        let _this = this;
        this.loading = true;
        this.scrollTop();

        this.pagination.page = typeof(page) == 'number'? page : 1;
        this.param.page = this.pagination.page;
        doAjax(ctx + "system/role/list", this.param, (data) => {
          if (data.state) {
            let pageData = data.data;
            _this.pagination.totalPages = pageData.pages; // 总页数
            _this.datatable.items = addIndexPropForArray(pageData.data, _this.pagination); // 数据集合
          } else {
            _this.toast(data.message, 'warning');
          }
        });
      }
    },

    /*
     * 重置查询表单
     */
    resetQueryForm() {
      this.$refs.queryForm.reset();
      this.doQuery();
    },

    /*
     * 打开表单编辑画面
     */
    openFormDialog(title, id) {
      this.formData.roleId = id || null;
      this.dialogTitle = title;
      this.winDialog = true;

      // 查询记录详情
      if (id) {
        this.posting = true;
        let _this = this;
        doAjax(ctx + "system/role/single/" + id, null, (data) => {
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
    },

    /*
     * 提交表单数据
     */
    doSubmit() {
      this.posting = true;
      let _this = this;

      let method = this.formData.roleId? "update" : "save";
      doAjax(ctx + "system/role/" + method, this.formData, (data) => {
        if (data.state) {
          _this.toast("操作成功");
          _this.closeFormDialog();
          _this.resetQueryForm();
        } else {
          _this.toast(data.message, 'warning');
        }
      });
    },

    /*
     * 删除数据
     */
    doDelete(roleId, confirmObj) {
      doAjaxGet(ctx + "system/role/del/" + roleId, null, (data) => {
        if (data.state) {
          this.toast("删除成功");
          this.doQuery();
        } else {
          this.toast(data.message, 'warning');
          confirmObj.finish();
        }
      });
    }
  }
}