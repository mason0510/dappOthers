<template>
    <div id="main" class="app-main">
        <router-view></router-view>
    </div>
</template>

<script>
    export default {
        data () {
            return {
                theme: this.$store.state.theme
            };
        },
        mounted () {

        },
        beforeDestroy () {

        },
        methods: {

        }
    };
</script>

<style>
.app-main{
    width: 100%;
    height: 100%;
}
</style>
<style lang="less">
	.big-model {
		.ivu-modal {
			top: 5vh;
		}
		.ivu-modal-body {
			overflow: auto;
			max-height: 75vh;
		}
	}
</style>
