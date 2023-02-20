<template>
  <div>
    <Row :gutter="10">
      <Card>
        <p slot="title" style="height: 120px;">
          <yztz-upload-image :uploadUrl="uploadUrl"
          :uploadType="3"
          @onUploaded="onUploaded" ></yztz-upload-image>
        </p>
        <yztz-table :columns="columns"
                    :tableData="tableData"
                    :loading="loading"
                    :total="total"
                    :pageSize="pageSize"
                    :showOP = "false"
                    viewText = '复制'
                    @deleteRow="deleteRow"
                    @changePage="changePage"
                    @viewRow="viewRow"
                    @pageSizeChange="pageSizeChange">

        </yztz-table>
      </Card>
      <yztz-preview-image
        @closePreview="closePreview"
        :imageUrl="showPreviewImageUrl"
        :imageShow="showPreviewImage">
      </yztz-preview-image>
      <Spin size="large" fix v-if="spinShow"></Spin>
    </Row>
  </div>
</template>

<script>

  //import tableColumns from './columns.js';
  // import tinymce from 'tinymce';
  import Row from "../../../node_modules/iview/src/components/grid/row.vue";

  import {fileUploadUrl} from '../../api/system'
  import {infoImageUrl} from '../../api/imageUrl'

  import Clipboard from 'clipboard';

  export default {
    components: {'Row':Row},
    data () {
      return {
        //图片查看
        showPreviewImage:false,
        showPreviewImageUrl:'',
        imgUrl:'',
        uploadUrl:fileUploadUrl,
        data:{},


        //
        spinShow:false,
        //搜索

        //table
        columns: [],
        loading: false,
        tableData: [],
        pageSize:20,
        pageIndex:1,
        total:0
      };
    },
    methods: {

      getData(){

        this.columns = [
          {
            title: '',
            type: 'index',
            width: 60,
            align: 'center'
          },
          {
            title: '图片',
            width: 140,
            align: 'center',
            render: (h, params) => {
              const row = params.row;
              let photo = row.photo;
              if (photo!=''){
                let imgUrl = infoImageUrl + photo
                return h('img', {
                 attrs: {
                    src: imgUrl  + '?imageView2/1/w/100/h/100',
                  },
                  style: {
                    display:'inline-block',
                    // border:'3px solid',
                    cursor:'pointer',
                    marginTop: '5px'
                  },
                  on: {
                      click: () => {
                          this.showPhoto(imgUrl)
                      }
                  }
                  //
                })
              }else{
                return '';
              }
            }
          },
          {
            title: '地址(点击复制)',
            align: 'center',
            render: (h, params) => {
              const row = params.row;
              let imgUrl = infoImageUrl + row.photo

              return h('div', {
               attrs: {
                  id: 'div'  + params.index,
                  'data-clipboard-text':imgUrl,
                  'title':'点击复制'
                },
                style: {
                  cursor:'pointer'
                },
                //
              },imgUrl);

            }
          },
          {
            title: '操作',
            align: 'center',
            width: 100,
            key: 'handle',
            handle: ['delete']
          }
        ];

        // this.tableData= [{'photo' :  'infomation/77c50eb8-cd4b-4dcb-8a38-3bdfb6d200ce'}]

        for(let i in this.tableData){
          let divId = '#div' + i
          let clipboard = new Clipboard(divId);
          let that = this;
          clipboard.on('success', (e)=> {

          });
        }

      },
      // Component Add-Panel

      // 表格翻页
      changePage (index) {
        // 这里直接更改了模拟的数据，真实使用场景应该从服务端获取数据
        this.pageIndex = index;
        this.getData();
      },
      // 表格每页数量切换
      pageSizeChange(pagesize){
        this.pageIndex = 1;
        this.pageSize = pagesize;
        this.getData();
      },

      // 删除表格
      deleteRow(row,index){

        this.tableData.splice(index, 1);

      },
      // 查看表格
      viewRow(row,index){

        let clipBoardContent = infoImageUrl + row.photo;
        let rowId = '#div' + index;

        // let clipboard = new Clipboard(rowId,{
        //   text: ()=>"这里是需要复制的内容";
        // });

        // this.window.clipboardData.setData("Text",clipBoardContent);
        // this.$message({
        //   message: '已复制好，可贴粘。',
        //   type: 'success'
        // })

      },

      // 显示头像
      showPhoto(photoUrl){

          this.showPreviewImage = true;
          this.showPreviewImageUrl = photoUrl;

      },

      onUploaded(fileName){

        let img = {'photo' :  fileName};
        this.tableData.splice(0,0,img);
        this.getData();

      },
      //删除预览
      handleRemove(item){
        this.imgUrl = '';
      },
      //预览
      handleView(item){
        this.showPreviewImage = true;
        this.showPreviewImageUrl = infoImageUrl + this.imgUrl;


      },
      //关闭预览
      closePreview(){
        this.showPreviewImage = false;
        this.showPreviewImageUrl = '';
      }
    },
    computed: {

    },
    mounted () {
      this.getData();

    }
  };
</script>
