<template>
  <div>
    <Row :gutter="10">
      <Card>
        <yztz-add-panel :searchTxt="searchTxt" placeholder="你好！" v-on:handleSearch="handleSearch" v-on:add="handleAdd"></yztz-add-panel>
        <!--<yztz-table :columns="columns" v-model="tableData" @selectionChange=selectionChange></yztz-table>-->

        <yztz-table :columns="columns"
                    :tableData="tableData"
                    :content="self"
                    :loading="loading"
                    :total="total"
                    :pageSize="pageSize"
                    @changePage="changePage"
                    @editRow=editRow
                    @deleteRow="deleteRow"
                    @viewRow="viewRow"
                    @selectionChange=selectionChange
                    @pageSizeChange="pageSizeChange">

        </yztz-table>
      </Card>

      <Modal
        v-model="showModal"
        :title="modalTitle" :mask-closable="false"
        width="900">
        <Form :label-width="80" :model="editForm" ref="editForm" :rules="editFormValidate">
          <FormItem label="标题" prop="title">
            <Input v-model="editForm.title"  />
          </FormItem>
          <FormItem label="文章来源" prop="source">
            <Input v-model="editForm.source"  />
          </FormItem>
          <Row>
            <Col span="5">
            <FormItem label="大类型" >
              <Select  style="width:90px" v-model="editForm.bigType">
                <Option v-for="item in catalog1" :value="item.bigType" :key="item.bigType">{{ item.name }}</Option>
              </Select>
            </FormItem>
            </Col>
            <Col span="5">
            <FormItem label="类型" >
              <Select  style="width:90px" v-model="editForm.type">
                <Option v-for="item in catalog" :value="item.value" :key="item.value">{{ item.value }}</Option>
              </Select>
            </FormItem>
            </Col>
            <Col span="5">
            <FormItem label="国家" >
              <Select  style="width:90px" v-model="editForm.country">
                <Option v-for="item in country" :value="item.value" :key="item.value">{{ item.value }}</Option>
              </Select>
            </FormItem>
            </Col>
            <Col span="5">
            <FormItem label="地区" >
              <Select  style="width:90px" v-model="editForm.area">
                <Option v-for="item in area" :value="item.value" :key="item.value">{{ item.label }}</Option>
              </Select>
            </FormItem>
            </Col>
            <Col span="9">
            </Col>
          </Row>

          <FormItem label="图片">
            <yztz-upload-image :uploadUrl="uploadUrl" @onProgress="onProgress" @onError="onError" @onSuccess="onSuccess" ></yztz-upload-image>
          </FormItem>

          <FormItem label="正文" prop="content">
            <div class="margin-top-20">
              <textarea id="articleEditor" v-model="editForm.content"></textarea>
            </div>
          </FormItem>
          <FormItem label="摘要" >
            <Input type="textarea"  v-model="editForm.summary" :autosize="true" ></Input>
          </FormItem>
          <FormItem label="来源地址" >
            <Input v-model="editForm.url"  />
          </FormItem>
          <FormItem label="SEO标题" >
            <Input v-model="editForm.seoTitle"  />
          </FormItem>
          <FormItem label="SEO关键字" >
            <Input v-model="editForm.seoKeywords"  />
          </FormItem>
          <FormItem label="SEO描述" >
            <Input v-model="editForm.seoComment"  />
          </FormItem>
        </Form>
        <div slot="footer">
          <Button   @click="handelCancel">取消</Button>
          <Button type="primary"  @click="handelSubmit">提交</Button>
        </div>
      </Modal>
      <Spin size="large" fix v-if="spinShow"></Spin>
    </Row>
  </div>
</template>

<script>

  import tableColumns from './columns.js';
  import tinymce from 'tinymce';
  import Row from "../../../node_modules/iview/src/components/grid/row.vue";
  import {getInfoList} from '../../api/application'
  export default {
    components: {Row},
    data () {
      return {
        data:{},
        self:this,
        //
        spinShow:false,
        //搜索
        searchTxt : '',
        //table
        columns: [],
        loading: false,
        tableData: [],
        pageSize:20,
        total:35,
        //编辑
        showModal:false,
        modalTitle:'',
        //表单内容
        editFormValidate: {
          title: [
            { required: true, message: '标题不能为空', trigger: 'blur' }
          ],
          source: [
            { required: true, message: '来源不能为空', trigger: 'blur' }
          ]
        },
        catalog1:[
          {name:'城市概况',bigType:1100},
          {name:'购房准备',bigType:1200},
          {name:'交易环节',bigType:1300},
          {name:'热门百科',bigType:1400},
        ],
        catalog: [{value: '城市概况'},
          {value: '购房准备'},
          {value: '交易环节'}],
        country:[{value: '美国'}, {value: '加拿大'}],
        area:[{value: '',label:'不限'}],
        editForm:{
          title:'',
          catalog:'城市概况',
          country:'',
          area:'',
          source:'',
          url:'',
          summary:'',
          content:'',
          seoTitle:'',
          seoKeywords:'',
          seoComment:''
        },
        uploadUrl:''

      };
    },
    methods: {
      // 初始化信息
      getData () {
        this.loading = true;
        let data = this.data = {"total":378,"rows":[{"answerNum":0,"bannerRecommend":0,"country":"ca","createdAt":{"date":2,"day":4,"hours":16,"minutes":39,"month":10,"seconds":50,"time":1509611990047,"timezoneOffset":-480,"year":117},"hotRecommend":0,"id":"59fad9d6d55c151928764a44","images":["4b8052d8-04fb-442f-ad5a-ab560c3600ea"],"labels":[],"nickName":"神一样的Man","region":"ca0201","remark":"","status":0,"title":"今生今世好多好多好多话","updatedAt":{"date":2,"day":4,"hours":16,"minutes":39,"month":10,"seconds":50,"time":1509611990047,"timezoneOffset":-480,"year":117},"userId":"599a3d88d55c1552be64d28c"},{"answerNum":0,"bannerRecommend":0,"country":"ca","createdAt":{"date":2,"day":4,"hours":16,"minutes":39,"month":10,"seconds":30,"time":1509611970377,"timezoneOffset":-480,"year":117},"hotRecommend":0,"id":"59fad9c2d55c151928764a43","images":["1a4fc4f3-64ef-4e37-80ba-1f71178cd8ba","af14f906-8ecc-4a30-8390-b21bf62678d1"],"labels":[],"nickName":"神一样的Man","region":"ca0201","remark":"","status":0,"title":"就是基督教基督教世界","updatedAt":{"date":2,"day":4,"hours":16,"minutes":39,"month":10,"seconds":30,"time":1509611970377,"timezoneOffset":-480,"year":117},"userId":"599a3d88d55c1552be64d28c"},{"answerNum":0,"bannerRecommend":0,"country":"ca","createdAt":{"date":2,"day":4,"hours":16,"minutes":39,"month":10,"seconds":12,"time":1509611952845,"timezoneOffset":-480,"year":117},"hotRecommend":0,"id":"59fad9b0d55c151928764a42","images":["ffbd0e1c-9a6b-4b24-819e-b711c67fe2b3","495f84ff-2b2a-4050-bfb5-50e006504a54","88a88179-8d7b-4ace-984d-6abf8f6f76a0"],"labels":[],"nickName":"神一样的Man","region":"ca0201","remark":"","status":0,"title":"计算机化睡觉睡觉睡觉","updatedAt":{"date":2,"day":4,"hours":16,"minutes":39,"month":10,"seconds":12,"time":1509611952845,"timezoneOffset":-480,"year":117},"userId":"599a3d88d55c1552be64d28c"},{"answerNum":0,"bannerRecommend":0,"country":"ca","createdAt":{"date":2,"day":4,"hours":16,"minutes":38,"month":10,"seconds":57,"time":1509611937139,"timezoneOffset":-480,"year":117},"hotRecommend":0,"id":"59fad9a1d55c151928764a41","images":["f5ab74c0-297f-4040-900e-0dc829cd3d07"],"labels":[],"nickName":"神一样的Man","region":"ca0201","remark":"","status":0,"title":"今生今世救救我","updatedAt":{"date":2,"day":4,"hours":16,"minutes":38,"month":10,"seconds":57,"time":1509611937139,"timezoneOffset":-480,"year":117},"userId":"599a3d88d55c1552be64d28c"},{"answerNum":0,"bannerRecommend":0,"country":"ca","createdAt":{"date":2,"day":4,"hours":16,"minutes":0,"month":10,"seconds":54,"time":1509609654143,"timezoneOffset":-480,"year":117},"hotRecommend":0,"id":"59fad0b6d55c15183c8dee86","images":["b09f07fe-c6c0-4e42-b310-ccb8b6ee47df"],"labels":[],"nickName":"神一样的Man","region":"ca0201","remark":"","status":0,"title":"哈哈哈有意思么？我","updatedAt":{"date":2,"day":4,"hours":16,"minutes":0,"month":10,"seconds":54,"time":1509609654143,"timezoneOffset":-480,"year":117},"userId":"599a3d88d55c1552be64d28c"},{"answerNum":0,"bannerRecommend":0,"country":"us","createdAt":{"date":2,"day":4,"hours":15,"minutes":38,"month":10,"seconds":59,"time":1509608339261,"timezoneOffset":-480,"year":117},"hotRecommend":0,"id":"59facb93d55c15183c8dee84","images":["135f0026-5c81-40cb-9461-dcb1cb35d64c","919fe562-f62f-43c1-a5bb-7de3da78320d","7e4a29df-30ff-414f-9442-75d0b9ac46d1"],"labels":[],"nickName":"神一样的Man","region":"us0101","remark":"","status":0,"title":"来咯哦哦哦的","updatedAt":{"date":2,"day":4,"hours":15,"minutes":38,"month":10,"seconds":59,"time":1509608339261,"timezoneOffset":-480,"year":117},"userId":"599a3d88d55c1552be64d28c"},{"answerNum":1,"bannerRecommend":0,"country":"ca","createdAt":{"date":2,"day":4,"hours":15,"minutes":21,"month":10,"seconds":18,"time":1509607278400,"timezoneOffset":-480,"year":117},"hotRecommend":0,"id":"59fac76ed55c15170091be23","images":["409d8a84-56ef-4c1d-a438-4a4ec6edca08","94af6a30-0700-4c89-be8a-f03806473e33","609c5899-f8b1-4eb1-8f77-1a08358aa89e"],"labels":[],"nickName":"test01","region":"ca0101","remark":"","status":0,"title":"哈哈哈😄、在于他用过五花八门或简单又简单大方","updatedAt":{"date":2,"day":4,"hours":15,"minutes":21,"month":10,"seconds":18,"time":1509607278400,"timezoneOffset":-480,"year":117},"userId":"59eed69ad55c152b8fc495f2"},{"answerNum":4,"bannerRecommend":0,"country":"ca","createdAt":{"date":2,"day":4,"hours":15,"minutes":5,"month":10,"seconds":45,"time":1509606345007,"timezoneOffset":-480,"year":117},"hotRecommend":0,"id":"59fac3c9d55c15170091be1e","images":["32f935c7-ed83-4120-be2f-73dd081471dd"],"labels":[],"nickName":"test01","region":"ca0101","remark":"","status":0,"title":"我是Yunhao Wan？","updatedAt":{"date":2,"day":4,"hours":15,"minutes":5,"month":10,"seconds":45,"time":1509606345007,"timezoneOffset":-480,"year":117},"userId":"59eed69ad55c152b8fc495f2"},{"answerNum":0,"bannerRecommend":0,"country":"ca","createdAt":{"date":2,"day":4,"hours":15,"minutes":5,"month":10,"seconds":24,"time":1509606324708,"timezoneOffset":-480,"year":117},"hotRecommend":0,"id":"59fac3b4d55c15170091be1d","images":["776c83f4-e463-48ee-90ee-49c3f69a9512"],"labels":[],"nickName":"test01","region":"ca0101","remark":"","status":0,"title":"哈哈哈😄！哈哈哈😄","updatedAt":{"date":2,"day":4,"hours":15,"minutes":5,"month":10,"seconds":24,"time":1509606324708,"timezoneOffset":-480,"year":117},"userId":"59eed69ad55c152b8fc495f2"},{"answerNum":2,"bannerRecommend":0,"country":"ca","createdAt":{"date":2,"day":4,"hours":11,"minutes":35,"month":10,"seconds":28,"time":1509593728619,"timezoneOffset":-480,"year":117},"hotRecommend":0,"id":"59fa9280d55c150ba943679b","images":["caa9d286-dde1-4407-b022-652d439389b2"],"labels":[],"nickName":"test","region":"ca0201","remark":"图来看看","status":0,"title":"可口可乐了了来咯来咯","updatedAt":{"date":2,"day":4,"hours":11,"minutes":35,"month":10,"seconds":28,"time":1509593728619,"timezoneOffset":-480,"year":117},"userId":"59ee9534d55c152b8fc495de"},{"answerNum":2,"bannerRecommend":0,"country":"ca","createdAt":{"date":2,"day":4,"hours":11,"minutes":33,"month":10,"seconds":58,"time":1509593638233,"timezoneOffset":-480,"year":117},"hotRecommend":0,"id":"59fa9226d55c150ba9436796","images":["892658d2-5c97-48ba-b396-71c0f248a7a6"],"labels":[],"nickName":"test01","region":"ca0201","remark":"雅洁简介男","status":0,"title":"基督教和社会活动","updatedAt":{"date":2,"day":4,"hours":11,"minutes":33,"month":10,"seconds":58,"time":1509593638233,"timezoneOffset":-480,"year":117},"userId":"59eed69ad55c152b8fc495f2"},{"answerNum":0,"bannerRecommend":0,"country":"us","createdAt":{"date":1,"day":3,"hours":15,"minutes":36,"month":10,"seconds":27,"time":1509521787657,"timezoneOffset":-480,"year":117},"hotRecommend":0,"id":"59f9797bd55c150ba9436778","images":["16440795-e488-4d8d-ac25-96af27744b4f"],"labels":[],"nickName":"test01","region":"us0201","remark":"","status":0,"title":"运动风格烦烦烦","updatedAt":{"date":1,"day":3,"hours":15,"minutes":36,"month":10,"seconds":27,"time":1509521787657,"timezoneOffset":-480,"year":117},"userId":"59eed69ad55c152b8fc495f2"},{"answerNum":0,"bannerRecommend":0,"country":"ca","createdAt":{"date":1,"day":3,"hours":15,"minutes":36,"month":10,"seconds":26,"time":1509521786377,"timezoneOffset":-480,"year":117},"hotRecommend":0,"id":"59f9797ad55c150ba9436777","images":["11cc974c-0a77-4cd5-a74f-abc9e223fe44"],"labels":[],"nickName":"丁小雨","region":"ca0201","remark":"","status":0,"title":"啦啦啦啦啦啦啦","updatedAt":{"date":1,"day":3,"hours":15,"minutes":36,"month":10,"seconds":26,"time":1509521786377,"timezoneOffset":-480,"year":117},"userId":"59954b59d55c1552be64d254"},{"answerNum":0,"bannerRecommend":0,"country":"us","createdAt":{"date":1,"day":3,"hours":15,"minutes":36,"month":10,"seconds":8,"time":1509521768150,"timezoneOffset":-480,"year":117},"hotRecommend":0,"id":"59f97968d55c150ba9436776","images":["aa256b1b-e385-44bd-8947-d68a0fc9a7f3"],"labels":["生活"],"nickName":"test01","region":"us0201","remark":"","status":0,"title":"在乎过自己就是自己生活状态就是说我","updatedAt":{"date":1,"day":3,"hours":15,"minutes":36,"month":10,"seconds":8,"time":1509521768150,"timezoneOffset":-480,"year":117},"userId":"59eed69ad55c152b8fc495f2"},{"answerNum":0,"bannerRecommend":0,"country":"ca","createdAt":{"date":1,"day":3,"hours":15,"minutes":35,"month":10,"seconds":41,"time":1509521741573,"timezoneOffset":-480,"year":117},"hotRecommend":0,"id":"59f9794dd55c150ba9436774","images":["32f48fd7-7e03-4961-a573-72923b2498ec"],"labels":[],"nickName":"丁小雨","region":"ca0201","remark":"","status":0,"title":"离开了考虑考虑","updatedAt":{"date":1,"day":3,"hours":15,"minutes":35,"month":10,"seconds":41,"time":1509521741573,"timezoneOffset":-480,"year":117},"userId":"59954b59d55c1552be64d254"},{"answerNum":1,"bannerRecommend":0,"country":"us","createdAt":{"date":1,"day":3,"hours":15,"minutes":35,"month":10,"seconds":27,"time":1509521727846,"timezoneOffset":-480,"year":117},"hotRecommend":0,"id":"59f9793fd55c150ba9436773","images":["afa1a21b-73ab-42fc-94db-f93b1a1273e1"],"labels":[],"nickName":"test01","region":"us0201","remark":"","status":0,"title":"头发会更广泛的合作与竞争","updatedAt":{"date":1,"day":3,"hours":15,"minutes":35,"month":10,"seconds":27,"time":1509521727846,"timezoneOffset":-480,"year":117},"userId":"59eed69ad55c152b8fc495f2"},{"answerNum":0,"bannerRecommend":0,"country":"us","createdAt":{"date":1,"day":3,"hours":15,"minutes":34,"month":10,"seconds":43,"time":1509521683031,"timezoneOffset":-480,"year":117},"hotRecommend":0,"id":"59f97913d55c150ba9436772","images":["72ab3f61-bc9b-4668-8f49-1bf9ef7574d6"],"labels":[],"nickName":"test01","region":"us0201","remark":"","status":0,"title":"简直是大喊大叫的家","updatedAt":{"date":1,"day":3,"hours":15,"minutes":34,"month":10,"seconds":43,"time":1509521683031,"timezoneOffset":-480,"year":117},"userId":"59eed69ad55c152b8fc495f2"},{"answerNum":0,"bannerRecommend":0,"country":"us","createdAt":{"date":1,"day":3,"hours":15,"minutes":33,"month":10,"seconds":53,"time":1509521633620,"timezoneOffset":-480,"year":117},"hotRecommend":0,"id":"59f978e1d55c150ba9436771","images":["6e7af822-23a0-49de-bfb2-999699c2984d"],"labels":[],"nickName":"test01","region":"us0201","remark":"","status":0,"title":"和活活泼泼以后还会","updatedAt":{"date":1,"day":3,"hours":15,"minutes":33,"month":10,"seconds":53,"time":1509521633620,"timezoneOffset":-480,"year":117},"userId":"59eed69ad55c152b8fc495f2"},{"answerNum":0,"bannerRecommend":0,"country":"ca","createdAt":{"date":1,"day":3,"hours":15,"minutes":31,"month":10,"seconds":22,"time":1509521482845,"timezoneOffset":-480,"year":117},"hotRecommend":0,"id":"59f9784ad55c150ba943676f","images":["c64bfded-2f63-44bb-a7fd-c57ebcfcbd6c"],"labels":[],"nickName":"神一样的Man","region":"ca0201","remark":"","status":0,"title":"还会健健康康快乐","updatedAt":{"date":1,"day":3,"hours":15,"minutes":31,"month":10,"seconds":22,"time":1509521482845,"timezoneOffset":-480,"year":117},"userId":"599a3d88d55c1552be64d28c"},{"answerNum":0,"bannerRecommend":0,"country":"ca","createdAt":{"date":1,"day":3,"hours":15,"minutes":29,"month":10,"seconds":48,"time":1509521388090,"timezoneOffset":-480,"year":117},"hotRecommend":0,"id":"59f977ecd55c150ba943676e","images":["ec6a2eaf-ee0c-4995-9428-be1691e67213","5b3945c5-fa2d-4d49-942f-52a4e1f88d60"],"labels":[],"nickName":"神一样的Man","region":"ca0201","remark":"发个广告过后","status":0,"title":"让人头疼又有屁","updatedAt":{"date":1,"day":3,"hours":15,"minutes":29,"month":10,"seconds":48,"time":1509521388090,"timezoneOffset":-480,"year":117},"userId":"599a3d88d55c1552be64d28c"}],"success":true};
        this.tableData = data.rows;
        this.total = data.total;
        console.log(this.tableData);
        this.columns = tableColumns.qaColumns;
        this.loading = false;

      },
      // Component Add-Panel
      handleSearch(keywords){
        this.searchTxt = keywords;
        alert(this.searchTxt);
      },
      handleAdd(){
        this.modalTitle = "新增";
        this.showModal = true;
      },
      // 表格内容
      //表格行 选中后变化
      selectionChange(selection){
        alert(JSON.stringify(selection));
      },
      // 编辑表格
      editRow(row,index){
        this.modalTitle = "编辑-资讯";
        this.showModal = true;
        this.editForm=Object.assign({}, row);
      },
      // 删除表格
      deleteRow(row,index){
        this.data.rows.splice(index, 1);
        //alert(JSON.stringify(row));
      },
      // 查看表格
      viewRow(row,index){

        alert(JSON.stringify(row));

      },
      // 表格翻页
      changePage (index) {
        // 这里直接更改了模拟的数据，真实使用场景应该从服务端获取数据
        alert(index);
      },
      // 表格每页数量切换
      pageSizeChange(pagesize){
        alert(pagesize);
      },
      //Model 处理
      handelSubmit(){

        this.$refs['editForm'].validate((valid) => {
          if (valid) {
//
            this.showModal = false;
            this.$Message.success('提交成功!');
          } else {
            this.$Message.error('表单验证失败!');
          }
        })


      },
      handelCancel(){
        this.showModal = false;
      },
      // 文件上传
      onProgress(event, file, fileList){
        //
      },
      onSuccess(response, file, fileList){
        //
      },
      onError(error, file, fileList){
        //
      },
    },
    computed: {

    },
    mounted () {
      tinymce.init({
        selector: '#articleEditor',
        branding: false,
        elementpath: false,
        height: 300,
        language: 'zh_CN.GB2312',
        menubar: 'edit insert view format table tools',
        theme: 'modern',
        plugins: [
          'advlist autolink lists link image charmap print preview hr anchor pagebreak imagetools',
          'searchreplace visualblocks visualchars code fullscreen fullpage',
          'insertdatetime media nonbreaking save table contextmenu directionality',
          'emoticons paste textcolor colorpicker textpattern imagetools codesample'
        ],
        toolbar1: ' newnote print fullscreen preview | undo redo | insert | styleselect | forecolor backcolor bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link image emoticons media codesample',
        autosave_interval: '20s',
        image_advtab: true,
        table_default_styles: {
          width: '100%',
          borderCollapse: 'collapse'
        }
      });
      this.$nextTick(function (){
        this.getData();
      })
    },
    destroyed () {
      tinymce.get('articleEditor').destroy();
    }
  };
</script>


