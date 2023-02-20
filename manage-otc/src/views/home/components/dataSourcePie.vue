<template>
    <div style="width:100%;height:100%;" :id="elementId"></div>
</template>

<script>
const echarts = require('echarts');

export default {
    name: 'dataSourcePie',
    props: {
      legendData : Array,
      seriesData : Array,
      elementId : String

    },
    data () {
        return {
            //
        };
    },
    methods: {
      init(){

        
        var dataSourcePie = echarts.init( document.getElementById( this.elementId ));
        const option = {
            tooltip: {
                trigger: 'item',
                formatter: '{a} <br/>{b} : {c} ({d}%)'
            },
            legend: {
                orient: 'vertical',
                left: 'right',
                data: this.legendData
            },
            series: [
                {
                    name: '访问来源',
                    type: 'pie',
                    radius: '66%',
                    center: ['50%', '60%'],
                    data: this.seriesData,
                    itemStyle: {
                        emphasis: {
                            shadowBlur: 10,
                            shadowOffsetX: 0,
                            shadowColor: 'rgba(0, 0, 0, 0.5)'
                        }
                    }
                }
            ]
        };
        dataSourcePie.setOption(option);
        window.addEventListener('resize', function () {
            dataSourcePie.resize();
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
