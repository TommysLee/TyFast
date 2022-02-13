/*
 * 用户的业务逻辑
 */
const userMixin = {
  data: function() {
    return {
      menuName: "用户管理",
      // 查询条件
      param: {
        loginName: null,
        status: null
      },
      // 数据表格
      datatable: {
        headers: [
          { text: '序号', value:'index', align:"center"},
          { text: '账号', value:'loginName'},
          { text: '用户类型', value:'usertype', align:"center"},
          { text: '姓名', value:'realName'},
          { text: '状态', value:'status', align:"center"},
          { text: '最后登录IP', value:'loginIp'},
          { text: '最后登录时间', value:'loginTime', width:180},
          { text: '创建时间', value:'createTime', align:"center", width:180},
          { text: '操作', value:'operation', align:"center"}
        ],
        items: []
      },
      // 表单数据
      formData: {
        userId: null,
        loginName: null,
        userType: null,
        realName: null,
        sex: null,
        phone: null,
        email: null,
        status: null,
        remark: null,
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
    doQuery() {
      if (!this.loading) {
        this.loading = true;
        this.scrollTop();

        this.param.page = this.pagination.page;
        doAjax(ctx + "system/user/list", this.param, (data) => {
          if (data.state) {
            let pageData = data.data;
            this.pagination.totalPages = pageData.pages; // 总页数
            this.datatable.items = addIndexPropForArray(pageData.data, this.pagination); // 数据集合
          } else {
            this.toast(data.message, 'warning');
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
      this.formData.userId = id || null;
      this.dialogTitle = title;
      this.winDialog = true;

      // 查询记录详情
      if (id) {
        this.posting = true;
        doAjax(ctx + "system/user/single/" + id, null, (data) => {
          if (data.state) {
            this.copyValue(this.formData, data.data);
          } else {
            this.toast(data.message, 'warning');
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
      let method = this.formData.userId? "update" : "save";
      doAjax(ctx + "system/user/" + method, this.formData, (data) => {
        if (data.state) {
          this.toast("操作成功");
          this.closeFormDialog();
          this.resetQueryForm();
        } else {
          this.toast(data.message, 'warning');
        }
      });
    },

    /*
     * 删除数据
     */
    doDelete(userId) {
      this.posting = true;
      doAjax(ctx + "system/user/del/" + userId, null, (data) => {
        this.$refs[userId].isActive=false;
        this.toast("删除成功");
        this.doQuery();
      }, "GET");
    }
  }
};
