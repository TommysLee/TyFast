/*
 * 数据字典业务逻辑
 */
const dictMixin = {
  el: "#app",
  mixins,
  data: function() {
    return {
      menuName: "数据字典",
      // 查询条件
      param: {
        code: null,
        name: null
      },
      // 数据表格
      datatable: {
        headers: [
          { text: '序号', value:'index', align:"center"},
          { text: '字典Code', value:'code'},
          { text: '字典名称', value:'name'},
          { text: '字典项数量', value:'items', align:"center", width:100},
          { text: '创建时间', value:'createTime', align:"center", width:180},
          { text: '更新时间', value:'updateTime', align:"center", width:180},
          { text: '操作', value:'operation', align:"center"}
        ],
        items: []
      },
      // 表单数据
      formData: {
        code: null,
        name: null,
        oldCode: null
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
        this.loading = true;
        this.scrollTop();

        this.pagination.page = typeof(page) == 'number'? page : 1;
        this.param.page = this.pagination.page;
        doAjax(ctx + "system/dict/list", this.param, (data) => {
          if (data.state) {
            let pageData = data.data;
            this.pagination.totalPages = pageData.pages; // 总页数
            pageData.data && pageData.data.map(item => {
              item.items = parseJSON(item.items, []);
              return item;
            });
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
      this.resetForm('queryForm');
      this.doQuery();
    },

    /*
     * 打开表单编辑画面
     */
    openFormDialog(title, id, isUpdate) {
      this.formData.code = id || null;
      this.dialogTitle = title;
      this.winDialog = true;

      // 查询记录详情
      if (isUpdate) {
        this.posting = true;
        this.isUpdate = isUpdate;
        doAjaxGet(ctx + "system/dict/single/" + id, null, (data) => {
          if (data.state) {
            this.copyValue(this.formData, data.data);
          } else {
            this.toast(data.message, 'warning');
          }
        });
      }
    },

    /*
     * 关闭表单编辑画面
     */
    closeFormDialog() {
      this.winDialog = false;
      this.isUpdate = false;
      this.formData.oldCode = null;
      this.resetForm();
    },

    /*
     * 提交表单数据
     */
    doSubmit() {
      this.posting = true;
      let method = this.isUpdate? "update" : "save";
      doAjax(ctx + "system/dict/" + method, this.formData, (data) => {
        if (data.state) {
          this.toast(this.$t("操作成功"));
          this.closeFormDialog();
          this.doQuery();
        } else {
          this.toast(data.message, 'warning');
        }
      });
    },

    /*
     * 删除数据
     */
    doDelete(code) {
      doAjaxGet(ctx + "system/dict/del?code=" + code, null, (data) => {
        if (data.state) {
          this.toast(this.$t("操作成功"));
          this.doQuery();
        } else {
          this.toast(data.message, 'warning');
        }
      });
    },

    /*
     * 刷新字典缓存
     */
    reloadDict() {
      doAjaxPost(ctx + "system/dict/reload", null, () => {
        this.toast(this.$t("缓存刷新成功"));
      });
    }
  }
};
