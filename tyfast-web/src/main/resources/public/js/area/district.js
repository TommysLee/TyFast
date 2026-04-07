// 初始化Vue
const app = Vue.createApp({
  extends: baseApp,
  data() {
    return {
      menuName: "区县",
      // 查询条件
      param: {
        province: null,
        city: null
      },
      // 数据表格
      datatable: {
        headers: [
          { title: '#', value:'index', align:"center", width: 60},
          { title: '区县ID', value:'districtId', align:"center"},
          { title: '区县名称', value:'districtName'},
          { title: '备注', value:'remark'},
          { title: '操作', value:'operation', align:"center"}
        ],
        items: [],
        search: null
      },
      // 表单数据
      formData: {
        districtId: null,
        cityId: null,
        districtName: null,
        remark: null,
      },
      // 模态窗口
      winDialog: false,
      dialogTitle: '',
      operate: null,

      // 省列表
      provinceList: [],
      // 城市列表
      cityList: []
    }
  },

  /*
   * 加载列表数据
   */
  created() {
    // 加载省数据
    doAjaxGet(this.url("/area/province/list"), null, (result) => {
      this.provinceList = result.data;
      if (this.provinceList.length > 0) {
        this.param.province = this.provinceList[0];
        this.loadCityList();
      }
    });
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
     * 加载城市列表
     */
    loadCityList() {
      this.cityList = [];
      if (this.param.province) {
        this.posting = true;
        let params = {page: 1, pageSize: 500, provinceId: this.param.province.provinceId};
        doAjax(this.url("/area/city/list"), params, (result) => {
          this.cityList = result.data.data || [];
          if (this.cityList.length > 0) {
            this.param.city = this.cityList[0];
            this.doQuery();
          } else {
            this.datatable.items = [];
          }
        });
      }
    },

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
      if (this.param.city?.cityId) {
        this.loading = true;
        this.scrollDTableTop();

        doAjaxGet(this.url("/area/district/list/" + this.param.city.cityId), null, (result) => {
          if (result.state) {
            this.datatable.items = result.data; // 数据集合
          } else {
            this.toast(result.message, 'warning');
          }
        });
      }
    },

    /*
     * 打开表单编辑画面
     */
    openFormDialog(title, id, op) {
      title = t(title) + " " + this.param.province.provinceName;
      title += (this.param.province.flag != 1? " · " + this.param.city.cityName : "") + " ";
      this.formData.districtId = id || null;
      this.dialogTitle = title + this.$t("区县");
      this.winDialog = true;
      this.operate = op || 'save';

      // 查询记录详情
      if (id) {
        this.posting = true;
        doAjaxGet(this.url("/area/district/single/" + id), null, (result) => {
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
      let method = this.operate;
      this.formData.cityId = this.param.city.cityId;

      doAjax(this.url("/area/district/" + method), this.formData, (result) => {
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
    doDelete(districtId) {
      doAjaxGet(this.url("/area/district/del/" + districtId), null, (result) => {
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