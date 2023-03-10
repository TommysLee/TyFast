// 初始化Vue
let app = new Vue({
  el: "#app",
  mixins,
  data: {
    menuName: "省/直辖市",
    // 查询条件
    param: {
      provinceName: null,
      flag: null
    },
    // 数据表格
    datatable: {
      headers: [
        { text: '序号', value:'index', align:"center", width: 60},
        { text: 'ID', value:'provinceId', align:"center"},
        { text: '名称', value:'provinceName'},
        { text: '行政级别', value:'flag', align:"center"},
        { text: '操作', value:'operation', align:"center"}
      ],
      items: []
    },
    // 表单数据
    formData: {
      provinceId: null,
      provinceName: null,
      flag: null,
      remark: null,
    },
    // 模态窗口
    winDialog: false,
    dialogTitle: null,
    operate: null,
    // 行政级别
    levels: [],
    assistHeight: 20,
    // 数据字典
    dictConfig: {
      "boroughtype": "levels"
    }
  },
  computed: {
    levelsMap() {
      return toMap(this.levels);
    }
  },

  /*
   * 加载列表数据
   */
  created() {
    this.doQuery();
  },

  mounted() {
    // 页面渲染完成后，计算辅助元素的总高度
    this.$nextTick(() => {
      this.assistHeight = calcAssistHeight();
    })
  },

  methods: {
    /*
     * 执行条件查询
     */
    doQuery() {
      if (!this.loading) {
        this.loading = true;
        this.scrollDTableTop('dataTable');

        doAjax(ctx + "area/province/list", this.param, (data) => {
          if (data.state) {
            this.datatable.items = addIndexPropForArray(data.data); // 数据集合
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
    openFormDialog(title, id, op) {
      this.formData.provinceId = id || null;
      this.dialogTitle = title;
      this.winDialog = true;
      this.operate = op || 'save';

      // 查询记录详情
      if (id) {
        this.posting = true;
        doAjaxGet(ctx + "area/province/single/" + id, null, (data) => {
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
      this.resetForm();
    },

    /*
     * 提交表单数据
     */
    doSubmit() {
      this.posting = true;
      let method = this.operate;
      doAjax(ctx + "area/province/" + method, this.formData, (data) => {
        if (data.state) {
          this.toast(this.$t("操作成功"));
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
    doDelete(provinceId, confirmObj) {
      doAjaxGet(ctx + "area/province/del/" + provinceId, null, (data) => {
        if (data.state) {
          this.toast(this.$t("操作成功"));
          this.doQuery();
        } else {
          this.toast(data.message, 'warning');
          confirmObj.finish();
        }
      });
    }
  }
});
