/*
 * 数据字典业务逻辑
 */
const dictMixin = {
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
          { title: '#', value:'index', align:"center"},
          { title: '字典Code', value:'code'},
          { title: '字典名称', value:'name'},
          { title: '字典项数量', value:'items', align:"center"},
          { title: '创建时间', value:'createTime', align:"center", width:180},
          { title: '更新时间', value:'updateTime', align:"center", width:180},
          { title: '操作', value:'operation', align:"center"}
        ],
        items: [],
        total: 0
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

  mounted() {
    // 页面渲染完成后，计算辅助元素的总高度
    this.$nextTick(() => {
      this.assistHeight = calcAssistHeight();
      if (!this.$refs['updatePermis'] && !this.$refs['delPermis'] && !this.$refs['dictViewPermis']) {
        this.datatable.headers.remove(6);
      }
    })
  },

  methods: {
    /*
     * 执行条件查询
     */
    doQuery(page) {
      if (!this.loading) {
        page = page??this.getFitPage();
        if (this.pagination.page !== page) {
          this.pagination.page = page;
        } else { // 这里的逻辑是在page不变的情况下，依然刷新数据表
          this.pagination.page = 0;
          this.$nextTick(() => {
            this.pagination.page = page;
          })
        }
      }
    },

    /*
     * 查询数据表
     */
    doQueryTable() {
      if (this.pagination.page > 0) {
        this.loading = true;
        this.scrollDTableTop();
        this.param.page = this.pagination.page;
        this.param.pageSize = this.pagination.pageSize;

        doAjax(this.url("/system/dict/list"), this.param, (result) => {
          if (result.state) {
            let pageData = result.data;
            this.datatable.total = pageData.total; // 总记录数
            pageData.data && pageData.data.map(item => {
              item.items = parseJSON(item.items, []);
              return item;
            });
            this.datatable.items = addIndexPropForArray(pageData.data, this.pagination); // 数据集合
          } else {
            this.toast(result.message, 'warning');
          }
        });
      }
    },

    /*
     * 重置查询表单
     */
    resetQueryForm(page) {
      if (this.method !== 'update') {
        this.resetForm('queryForm');
      }
      this.doQuery(page);
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
        doAjaxGet(this.url("/system/dict/single/" + id), null, (result) => {
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
      this.isUpdate = false;
      this.formData.oldCode = null;
      this.resetForm();
    },

    /*
     * 提交表单数据
     */
    doSubmit() {
      this.posting = true;
      this.method = this.isUpdate? "update" : "save";
      doAjaxPost(this.url("/system/dict/" + this.method), this.formData, (result) => {
        if (result.state) {
          this.toast("操作成功");
          this.closeFormDialog();
          this.resetQueryForm();
        } else {
          this.toast(result.message, 'warning');
        }
      });
    },

    /*
     * 删除数据
     */
    doDelete(code) {
      this.method = 'del';
      doAjaxGet(this.url("/system/dict/del?code=" + code), null, (result) => {
        if (result.state) {
          this.toast("操作成功");
          this.doQuery();
        } else {
          this.toast(result.message, 'warning');
        }
      });
    },

    /*
     * 刷新字典缓存
     */
    reloadDict() {
      doAjaxPost(this.url("/system/dict/reload"), null, () => {
        this.toast("缓存刷新成功");
      });
    }
  }
};
