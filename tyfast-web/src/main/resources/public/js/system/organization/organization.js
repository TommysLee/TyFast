// 初始化Vue
const app = Vue.createApp({
  extends: baseApp,
  mixins: [thirdConfigMixin],
  data() {
    return {
      menuName: "机构管理",
      // 数据表格
      datatable: {
        headers: {
          disabled: true,
          headers: [
            { title: '机构名称'},
            { title: '编码', align:"center"},
            { title: '地区', align:"center"},
            { title: '经纬度', align:"center"},
            { title: '创建时间', align:"center"},
            { title: '第三方应用配置', align:"center"},
            { title: '操作', align:"center"}
          ]
        },
        items: []
      },
      // 表单数据
      formData: {
        orgId: null,
        parentId: null,
        orgCode: null,
        orgName: null,
        provinceId: null,
        provinceName: null,
        cityId: null,
        cityName: null,
        lng: null,
        lat: null,
        remark: null,
      },
      formArea: {
        provinceList: [],
        cityList: [],
        province: null,
        city: null
      },
      // 模态窗口
      winDialog: false,
      dialogTitle: ''
    }
  },

  computed: {
    topOrgs() {
      let orgs = this.datatable.items.filter(currentValue => {
        return !currentValue.disabled && currentValue.parentId === "0";
      });
      orgs.unshift({orgId: '0', orgName: '根机构'});
      if (1 !== this.userType) {
        orgs[0].subtitle = '不可选中';
        orgs[0].disabled = true;
      }
      return orgs;
    },
    userType() {
      return parseInt((this.$refs.userType && this.$refs.userType.innerText) || 2);
    },
    plainUserOnUpdate() {
      return 1 !== this.userType && null != this.formData.orgId;
    }
  },

  watch: {
    "formArea.province": function (val) {
      this.formArea.city = null;
      this.formArea.cityList = [];
      if (val && val.provinceId) {
        doAjaxPost(this.url("/area/city/list"), {provinceId:val.provinceId, pageSize:100}, (result) => {
          this.formArea.cityList = result.data.data || [];
          let foundObject = this.formArea.cityList.find(item => item.cityId === this.formData.cityId);
          let isInArray = foundObject !== undefined;
          if(isInArray){
            this.formArea.city = {cityId: this.formData.cityId, cityName: this.formData.cityName};
          }else{
            if(this.formArea.province.provinceId)
              this.formArea.city = {cityId: this.formArea.cityList[0].cityId, cityName: this.formArea.cityList[0].cityName};
          }
        });
      }
    }
  },

  /*
   * 加载列表数据
   */
  created() {
    this.datatable.items.push(this.datatable.headers);
    this.doQuery();
    this.loadProvinceList();
  },

  methods: {
    doQuery() {
      if (!this.loading) {
        this.loading = true;
        this.scrollTop();

        doAjaxGet(this.url("/system/org/list"), null, (result) => {
          if (result.state) {
            // 将列表数据包装为树结构数据
            this.datatable.items = this.wrapTreeData(result.data, 'orgId', this.datatable.headers);
          } else {
            this.toast(result.message, 'warning');
          }
        });
      }
    },

    /*
     * 打开表单编辑画面
     */
    openFormDialog(title, id) {
      this.formData.orgId = id || null;
      this.dialogTitle = title;
      this.winDialog = true;

      // 查询记录详情
      if (id) {
        this.posting = true;
        doAjaxGet(this.url("/system/org/single/" + id), null, (result) => {
          if (result.state) {
            this.mergeValue(this.formData, result.data);
            this.formArea.province = {provinceId: this.formData.provinceId, provinceName: this.formData.provinceName};
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
      this.formData.provinceId = this.formArea.province && this.formArea.province.provinceId;
      this.formData.provinceName = this.formArea.province && this.formArea.province.provinceName;
      this.formData.cityId = this.formArea.city && this.formArea.city.cityId;
      this.formData.cityName = this.formArea.city && this.formArea.city.cityName;
      this.method = this.formData.orgId? "update" : "save";
      doAjaxPost(this.url("/system/org/" + this.method), this.formData, (result) => {
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
    doDelete(orgId) {
      this.method = "del";
      doAjaxGet(this.url("/system/org/del/" + orgId), null, (result) => {
        if (result.state) {
          this.toast("操作成功");
          this.doQuery();
        } else {
          this.toast(result.message, 'warning');
        }
      });
    },

    /*
     * 加载省列表
     */
    loadProvinceList() {
      doAjaxGet(this.url("/area/province/list"), null, (result) => {
        this.formArea.provinceList = result.data;
      });
    },

    /*
     * 地区名称
     */
    placeName(item) {
      return item.provinceName !== item.cityName? (item.provinceName + ' ' + (item.cityName || '')) : item.cityName;
    },

    /*
     * 经纬度坐标
     */
    lngLat(item) {
      return (item.lng || item.lat)? (item.lng + ', ' + item.lat) : '-';
    }
  }
});
const appInstance = baseApp.uses(app).mount('#app');
