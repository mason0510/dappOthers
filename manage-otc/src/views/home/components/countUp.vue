<template>
    <div>
        <p :class="className" :style="{textAlign: 'center', color: color, fontSize: countSize, fontWeight: countWeight}"><span v-cloak :id="idName">{{ startVal }}</span><span>{{ unit }}</span></p>
        <slot name="intro"></slot>
    </div>
</template>

<script>
import CountUp from 'countup';

function transformValue (val) {
    let endVal = 0;
    let unit = '';
    if (val < 100000) {
        endVal = val;
    } else if (val >= 1000 && val < 1000000) {
        endVal = parseInt(val / 1000);
        unit = 'K+';
    } else if (val >= 1000000 && val < 10000000000) {
        endVal = parseInt(val / 1000000);
        unit = 'M+';
    } else {
        endVal = parseInt(val / 1000000000);
        unit = 'B+';
    }
    return {
        val: endVal,
        unit: unit
    };
}

export default {
    data () {
        return {
            unit: '',
            demo: {},
            startVal:0,
            decimals:0
        };
    },
    name: 'countUp',
    props: {
        idName: String,
        className: String,
        endVal: {
            type:[Number,String],
            required: true
        },
        duration: {
            type: Number,
            default: 2
        },
        delay: {
            type: Number,
            default: 500
        },
        options: {
            type: Object,
            default: () => {
                return {
                    useEasing: true,
                    useGrouping: true,
                    separator: ',',
                    decimal: '.'
                };
            }
        },
        color: String,
        countSize: {
            type: String,
            default: '30px'
        },
        countWeight: {
            type: Number,
            default: 700
        },
        introText: [String, Number]
    },
    mounted(){
    	if(typeof this.endVal=='string'){
    		this.startVal='-'
    	}
    },
    watch: {
        endVal (val) {
            this.$nextTick(() => {
            setTimeout(() => {
                let res = transformValue(this.endVal);
                let endVal = res.val;
                this.unit = res.unit;
                let demo = {};
                if(!Number.isInteger(this.endVal)){
			    		this.decimals=2
			    	}
                this.demo = demo = new CountUp(this.idName, this.startVal, endVal, this.decimals, this.duration, this.options);
                if (!demo.error) {
                    demo.start();
                }
            }, this.delay);
        });
        }
    }
};
</script>
