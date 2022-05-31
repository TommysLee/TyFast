// 初始化Vue
let app = new Vue({
  el: "#app",
  data: {
    menuName: "城市管理",
    // 查询条件
    param: {
      cityName: null,
      provinceId: null,
      flag: null
    },
    // 数据表格
    datatable: {
      headers: [
        {text: '序号', value: 'index', align: "center", width: 60},
        {text: '城市ID', value: 'cityId'},
        {text: '省/直辖市', value: 'provinceName'},
        {text: '城市名称', value: 'cityName'},
        {text: '行政级别', value: 'flag', align: "center"},
        {text: '操作', value: 'operation', align: "center"}
      ],
      items: []
    },
    assistHeight: 20,
    // 表单数据
    formData: {
      cityId: null,
      provinceId: null,
      cityName: null,
      flag: null,
      remark: null,
    },
    // 模态窗口
    winDialog: false,
    dialogTitle: null,
    operate: null,
    // 行政级别
    levels: [
      {text: '地级市', value: 2},
      {text: '县级市', value: 3}
    ],
    // 省列表
    provinceList: []
  },
  computed: {
    levelsMap() {
      let map = {1: '直辖市'};
      for (let level of this.levels) {
        map[level.value] = level.text;
      }
      return map;
    }
  },

  /*
   * 加载列表数据
   */
  created() {
    this.doQuery();

    // 加载省数据
    doAjaxGet(ctx + "area/province/list", null, (data) => {
      this.provinceList = data.data;
    });
  },

  mounted() {
    // 页面渲染完成后，计算辅助元素的总高度
    this.$nextTick(() => {
      this.assistHeight = calcAssistHeight();
    })
  },

  methods: {
    /*计算辅助元素的总高度
     * 执行条件查询
     */
    doQuery(page) {
      if (!this.loading) {
        this.loading = true;
        this.scrollTop();

        this.pagination.page = typeof (page) == 'number' ? page : 1;
        this.param.page = this.pagination.page;
        doAjax(ctx + "area/city/list", this.param, (data) => {
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
    resetQueryForm(queryFlag) {
      this.$refs.queryForm.reset();
      if (false != queryFlag) {
        this.doQuery();
      }
    },

    /*
     * 打开表单编辑画面
     */
    openFormDialog(title, id, op) {
      this.formData.cityId = id || null;
      this.dialogTitle = title;
      this.winDialog = true;
      this.operate = op || 'save';

      // 查询记录详情
      if (id) {
        this.posting = true;
        doAjaxGet(ctx + "area/city/single/" + id, null, (data) => {
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
      this.resetForm('dataForm', 'observer');
    },

    /*
     * 提交表单数据
     */
    doSubmit() {
      this.posting = true;
      let method = this.operate;
      doAjax(ctx + "area/city/" + method, this.formData, (data) => {
        if (data.state) {
          this.toast("操作成功");
          this.resetQueryForm(false);
          this.$nextTick(() => {
            this.param.cityName = this.formData.cityName;
            this.closeFormDialog();
            this.doQuery();
          });
        } else {
          this.toast(data.message, 'warning');
        }
      });
    },

    /*
     * 删除数据
     */
    doDelete(cityId, confirmObj) {
      doAjaxGet(ctx + "area/city/del/" + cityId, null, (data) => {
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
});
