<template>
    <div style="width:100%;height:100%;" :id="elementId"></div>
</template>

<script>
const echarts = require('echarts');
export default {
    name: 'visiteVolume',
    props: {
      yAxisData : {
        default : ['Mon', 'Tues', 'Wed', 'Thur', 'Fri', 'Sat', 'Sun']
      },
      seriesData : {
        default : [
            {value: 453682, name: 'Mon', itemStyle: {normal: {color: '#2d8cf0'}}},
            {value: 879545, name: 'Tues', itemStyle: {normal: {color: '#2d8cf0'}}},
            {value: 2354678, name: 'Wed', itemStyle: {normal: {color: '#2d8cf0'}}},
            {value: 1598403, name: 'Thur', itemStyle: {normal: {color: '#2d8cf0'}}},
            {value: 543250, name: 'Fri', itemStyle: {normal: {color: '#2d8cf0'}}},
            {value: 1305923, name: 'Sat', itemStyle: {normal: {color: '#2d8cf0'}}},
            {value: 1103456, name: 'Sun', itemStyle: {normal: {color: '#2d8cf0'}}}
        ]
      },
      elementId : {
        default:'visite_volume_con'
      }

    },
    data () {
        return {
            visiteVolume:''
        };
    },
    methods: {
      init(){
      	if (this.visiteVolume != null && this.visiteVolume != "" && this.visiteVolume != undefined) {
        this.visiteVolume.dispose();
   			 }
      		this.visiteVolume = echarts.init(document.getElementById(this.elementId));
        let xAxisData = [];
        let data1 = [];
        let data2 = [];
        for (let i = 0; i < 20; i++) {
            xAxisData.push('类目' + i);
            data1.push((Math.sin(i / 5) * (i / 5 - 10) + i / 6) * 5);
            data2.push((Math.cos(i / 5) * (i / 5 - 10) + i / 6) * 5);
        }

        const option = {
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'shadow'
                }
            },
            grid: {
                top: 0,
                left: '2%',
                right: '4%',
                bottom: '3%',
                containLabel: true
            },
            xAxis: {
                type: 'value',
                boundaryGap: [0, 0.01]
            },
            yAxis: {
                type: 'category',
                data: this.yAxisData,
                nameTextStyle: {
                    color: '#c3c3c3'
                }
            },
            series: [
                {
                    name: '数量',
                    type: 'bar',
                    data: this.seriesData
                }
            ]
        };

       this.visiteVolume.setOption(option);
       let _this = this;
			window.addEventListener('resize', function () {
		            _this.visiteVolume.resize();
		        });
      }
    },
    watch: {
      seriesData (data) {
        this.init();
      }
    },
    mounted () {
        this.$nextTick(() => {
            this.init()
        });
    }
};
</script>
