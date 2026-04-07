// 初始化Vue
const app = Vue.createApp({
  extends: baseApp,
  data() {
    return {
      menuName: "视频监控",
      param: {
        org: null
      },
      videoList: [],
      selectedVideo: null,
      hdFlag: false,
      token: null,
      player: null
    }
  },

  watch: {
    "param.org": function() {
      this.doQuery();
    },
    availableOrgs(val) {
      if (val?.length) {
        this.param.org = val[0];
      } else {
        this.param.org = null;
      }
    },
    selectedVideo() {
      this.playV()
    },
    hdFlag() {
      this.playV()
    }
  },

  created() {
    // 加载可用的机构列表
    this.loadAvailableOrgs();
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
      if (this.param.org) {
        this.menuName = this.$t('视频监控') + " · " + this.param.org.orgName;
        this.loading = true;
        this.videoList = [];
        this.selectedVideo = null;
        doAjaxGet(this.url("/" + this.param.org.orgId + "/device/video/accesstoken/ys"), null, result => {
          if (result.state && result.data) {
            this.token = result.data.accessToken;
            this.loading = true;
            doAjaxGet(this.url("/" + this.param.org.orgId + "/device/video/list/all"), null, result => {
              if (result.state) {
                this.videoList = result.data || [];
                if (this.videoList.length > 0) {
                  this.selectedVideo = this.videoList[0];
                }
              }
            })
          } else {
            this.toast('请将萤石开放平台的配置添加到机构', 'warning');
          }
        })
      }
    },

    /*
     * 播放视频流
     */
    playV() {
      // 切换地址前，先执行stop方法停止上次取流
      if (this.player) {
        this.player.stop();
      }

      // 重新加载视频资源
      const url = this.hdFlag? this.selectedVideo?.monitorHdUrl : this.selectedVideo?.monitorUrl;
      this.$nextTick(() => {
        this.$refs.vlive.innerHTML = '';
        if (url) {
          this.player = new EZUIKit.EZUIKitPlayer({
            id: 'vlive',
            accessToken: this.token,
            url,
            template: 'simple', // simple - 极简版;standard-标准版;security - 安防版(预览回放);voice-语音版; theme-可配置主题；
            autoplay: true
          });
        } else {
          this.player = null;
        }
      })
    }
  }
});
const appInstance = baseApp.uses(app).mount('#app');