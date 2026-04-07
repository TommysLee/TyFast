// 初始化Vue
const app = Vue.createApp({
  extends: baseApp,
  data() {
    return {
      menuName: "城市",
      // 查询条件
      param: {
        cityName: null,
        provinceId: null,
        flag: null
      },
      // 数据表格
      datatable: {
        headers: [
          {title: '#', value: 'index', align: "center", width: 60},
          {title: '城市ID', value: 'cityId'},
          {title: '省/直辖市', value: 'provinceName'},
          {title: '城市名称', value: 'cityName'},
          {title: '行政级别', value: 'flag', align: "center"},
          {title: '操作', value: 'operation', align: "center"}
        ],
        items: [],
        total: 0
      },
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
      dialogTitle: '',
      operate: null,
      // 行政级别
      levels: [
        {title: '地级市', value: 2},
        {title: '县级市', value: 3}
      ],
      // 省列表
      provinceList: []
    }
  },
  computed: {
    levelsMap() {
      let map = toMap(this.levels);
      map[1] = '直辖市';
      return map;
    }
  },

  created() {
    // 加载省数据
    doAjaxGet(this.url("/area/province/list"), null, (result) => {
      this.provinceList = result.data;
    });
  },

  mounted() {
    // 页面渲染完成后，计算辅助元素的总高度
    this.$nextTick(() => {
      this.assistHeight = calcAssistHeight();
      if (!this.$refs['updatePermis'] && !this.$refs['delPermis']) {
        this.datatable.headers.remove(5);
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
    doQueryTable(page) {
      if (this.pagination.page > 0) {
        this.loading = true;
        this.scrollDTableTop();
        this.param.page = this.pagination.page;
        this.param.pageSize = this.pagination.pageSize;

        doAjax(this.url("/area/city/list"), this.param, (result) => {
          if (result.state) {
            let pageData = result.data;
            this.datatable.total = pageData.total; // 总记录数
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
    openFormDialog(title, id, op) {
      this.formData.cityId = id || null;
      this.dialogTitle = title;
      this.winDialog = true;
      this.operate = op || 'save';

      // 查询记录详情
      if (id) {
        this.posting = true;
        doAjaxGet(this.url("/area/city/single/" + id), null, (result) => {
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
      doAjax(this.url("/area/city/" + this.method), this.formData, (result) => {
        if (result.state) {
          this.toast("操作成功");
          this.closeFormDialog();
          this.param.cityName = this.formData.cityName;
          this.method = null;
          this.doQuery(1);
        } else {
          this.toast(result.message, 'warning');
        }
      });
    },

    /*
     * 删除数据
     */
    doDelete(cityId) {
      this.method = "del";
      doAjaxGet(this.url("/area/city/del/" + cityId), null, (result) => {
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
