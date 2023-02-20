<template>

    <p slot="title" style="height: 35px;line-height: 35px">
      <Input v-model="keyWords" v-show="showSearch" icon="search"  :placeholder="placeholder" style="width: 200px" />
      <span ><Button  style="width:90px;" v-show="showSearch"  type="ghost" @click="handleChangeSearch">搜索</Button></span>
      <span ><Button  style="width:90px;" type="primary" @click="handleAdd">新增</Button></span>
    </p>

</template>
<script>
    export default {
        props: {
          searchTxt : String,
          placeholder : String,
          showSearch: {
            default:true
          }
        },
      data () {
        return {
          keyWords:''
        };
      },
        methods:{
          handleChangeSearch(){
            this.$emit('handleSearch',this.keyWords)
          },
          handleAdd(){
            this.$emit('add')
          }
        },
      mounted () {
        this.keyWords = this.searchTxt;
      }
    }
</script>
<style scoped="">

</style>
