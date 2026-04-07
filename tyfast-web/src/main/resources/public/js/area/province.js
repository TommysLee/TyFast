// 初始化Vue
const app = Vue.createApp({
  extends: baseApp,
  data() {
    return {
      menuName: "省/直辖市",
      // 查询条件
      param: {
        provinceName: null,
        flag: null
      },
      // 数据表格
      datatable: {
        headers: [
          { title: '#', value:'index', align:"center", width: 60},
          { title: 'ID', value:'provinceId', align:"center"},
          { title: '名称', value:'provinceName'},
          { title: '行政级别', value:'flag', align:"center"},
          { title: '操作', value:'operation', align:"center"}
        ],
        items: [],
        search: null,
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
    }
  },
  computed: {
    levelsMap() {
      return toMap(this.levels);
    }
  },

  mounted() {
    // 页面渲染完成后，计算辅助元素的总高度
    this.$nextTick(() => {
      this.assistHeight = calcAssistHeight();
      if (!this.$refs['updatePermis'] && !this.$refs['delPermis']) {
        this.datatable.headers.remove(4);
      }
    })
  },

  methods: {
    /*
     * 执行条件查询
     */
    doQuery() {
      if (!this.loading) {
        this.datatable.search = String(Date.now())
      }
    },

    /*
     * 查询数据表
     */
    doQueryTable() {
      this.loading = true;
      this.scrollDTableTop();

      doAjax(this.url("/area/province/list"), this.param, (result) => {
        if (result.state) {
          this.datatable.items = addIndexPropForArray(result.data); // 数据集合
        } else {
          this.toast(result.message, 'warning');
        }
      });
    },

    /*
     * 重置查询表单
     */
    resetQueryForm() {
      if (this.method !== 'update') {
        this.resetForm('queryForm');
      }
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
        doAjaxGet(this.url("/area/province/single/" + id), null, (result) => {
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
    },

    /*
     * 提交表单数据
     */
    doSubmit() {
      this.posting = true;
      this.method = this.operate;
      doAjax(this.url("/area/province/" + this.method), this.formData, (result) => {
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
    doDelete(provinceId) {
      this.method = "del";
      doAjaxGet(this.url("/area/province/del/" + provinceId), null, (result) => {
        if (result.state) {
          this.toast("操作成功");
          this.doQuery();
        } else {
          this.toast(result.message, 'warning');
        }
      });
    }
  }
});
const appInstance = baseApp.uses(app).mount('#app');