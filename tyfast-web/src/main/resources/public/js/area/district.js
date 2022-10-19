// 初始化Vue
let app = new Vue({
  el: "#app",
  mixins,
  data: {
    menuName: "区县",
    // 查询条件
    param: {
      province: null,
      city: null
    },
    // 数据表格
    datatable: {
      headers: [
        { text: '序号', value:'index', align:"center", width: 60},
        { text: '区县ID', value:'districtId', align:"center"},
        { text: '区县名称', value:'districtName'},
        { text: '备注', value:'remark'},
        { text: '操作', value:'operation', align:"center"}
      ],
      items: []
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
    dialogTitle: null,
    operate: null,

    // 省列表
    provinceList: [],
    // 城市列表
    cityList: []
  },

  /*
   * 加载列表数据
   */
  created() {
    // 加载省数据
    doAjaxGet(ctx + "area/province/list", null, (data) => {
      this.provinceList = data.data;
      if (this.provinceList.length > 0) {
        this.param.province = this.provinceList[0];
        this.loadCityList(this.param.province);
      }
    });
  },

  methods: {
    /*
     * 加载城市列表
     */
    loadCityList(province) {
      this.cityList = [];
      if (province) {
        this.posting = true;
        let params = {page: 1, pageSize: 100, provinceId: province.provinceId};
        doAjax(ctx + "area/city/list", params, (data) => {
          this.cityList = data.data.data || [];
          if (this.cityList.length > 0) {
            this.param.city = this.cityList[0];
            this.doQuery(this.param.city);
          } else {
            this.datatable.items = [];
          }
        });
      }
    },

    /*
     * 执行条件查询
     */
    doQuery(city) {
      if (!this.loading && city) {
        this.loading = true;
        this.scrollTop();

        doAjax(ctx + "area/district/list/" + city.cityId, null, (data) => {
          if (data.state) {
            this.datatable.items = addIndexPropForArray(data.data); // 数据集合
          } else {
            this.toast(data.message, 'warning');
          }
        });
      }
    },

    /*
     * 打开表单编辑画面
     */
    openFormDialog(title, id, op) {
      title += " " + this.param.province.provinceName;
      title += (this.param.province.flag != 1? " · " + this.param.city.cityName : "");
      this.formData.districtId = id || null;
      this.dialogTitle = title + " 区县";
      this.winDialog = true;
      this.operate = op || 'save';

      // 查询记录详情
      if (id) {
        this.posting = true;
        doAjaxGet(ctx + "area/district/single/" + id, null, (data) => {
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
      this.formData.cityId = this.param.city.cityId;

      doAjax(ctx + "area/district/" + method, this.formData, (data) => {
        if (data.state) {
          this.toast(this.$t("操作成功"));
          this.closeFormDialog();
          this.doQuery(this.param.city);
        } else {
          this.toast(data.message, 'warning');
        }
      });
    },

    /*
     * 删除数据
     */
    doDelete(districtId, confirmObj) {
      doAjaxGet(ctx + "area/district/del/" + districtId, null, (data) => {
        if (data.state) {
          this.toast(this.$t("操作成功"));
          this.doQuery(this.param.city);
        } else {
          this.toast(data.message, 'warning');
          confirmObj.finish();
        }
      });
    }
  }
});
