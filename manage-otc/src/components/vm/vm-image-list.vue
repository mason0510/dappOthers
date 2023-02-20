<style lang="less">
  @import './components.less';
</style>

<template>
  <div class="vm-image-list">
    <Row class="image-list-heading vm-panel">
      <Row type="flex" align="middle" justify="space-between" class="panel-body">
       <div class="search-bar">

        </div>
        <Row type="flex" align="middle" class="page">
          <span>显示</span>
          <Input :max="40" :min="1" :number="true" v-model="showNum" class="input-number" @on-change=" updateDataShow "></Input>
          <span class="margin-end">/ 每页</span>
          <span class="total">共 {{ data.length }} 条</span>
          <Page :total="data.length" :current="currentPage" :page-size="showNum" @on-change="pageChange"></Page>
        </Row>
      </Row>
    </Row>

      <Row class="image-list" :gutter="16">
        <Col :lg="6" :sm="8" class="vm-margin" v-for="item in dataShow" :key="item.id">
          <VmCard :editable="true"
          :title="item.title"
          :img="item.img"
          :desc="item.desc"
          :detailUrl="item.detailUrl"
          :editUrl="item.editUrl"
          :isRecommend=item.isRecommend
          :isDelete=item.isDelete
          :id=item.id
          @delete-ok=" deleteOk(item) "
          @imgClicked=" imgClicked(item) "
          @recommendClicked=" recommendClicked(item) "></VmCard>
        </Col>
      </Row>


  </div>
</template>

<script>
  import VmCard from '@/components/vm/vm-card'
  export default {
    name: 'VmImageList',
    components: {
      VmCard
    },
    props: {
      title: {
        type: String,
        default: 'Image List'
      },
      // origin data
      data: {
        type: Array,
        default: function () {
          return [

          ]
        }
      }
    },
    data: function () {
      return {
        keyword: '', // keyword for search
        dataShow: [], // data for showing
        showNum: 9, // number of item per page
        currentPage: 1
      }
    },
    methods: {
      updateDataShow: function () {
        let startPage = (this.currentPage - 1) * this.showNum
        let endPage = startPage + this.showNum
        this.dataShow = this.data.slice(startPage, endPage)
      },
      pageChange: function (page) {
        this.currentPage = page
        this.updateDataShow()
      },
      search: function () {
        let that = this
        let tempData = that.data
        that.dataShow = []
        tempData.forEach(function (elem) {
          for (let i in elem) {
            if (elem[i].toString().indexOf(that.keyword) > -1) {
              that.dataShow.push(elem)
              return
            }
          }
        })
      },
      deleteOk: function (data) {
        this.$emit('delete-ok', data)
      },
      imgClicked: function (data) {
    
        this.$emit('imgClicked', data)
      },
      recommendClicked: function (data) {
       this.$emit('recommendClicked', data)
     }
    },
    watch: {
      data: function () {
        this.dataShow = this.data.slice(0, this.showNum) // update dataShow once data changed
      }
    },
    mounted: function () {
      this.dataShow = this.data.slice(0, this.showNum)
    }
  }
</script>
