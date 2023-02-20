<template>
	<div>
		<Row :gutter="10">
      <Card>
				<p slot="title" style="height: 40px;line-height: 35px">
					<span ><Button  style="width:200px;" type="primary" @click="goDownload()">下载USDT未签名交易</Button></span>
				</p>
        <p slot="title" style="height: 40px;line-height: 35px">
          <span ><Button  style="width:200px;" type="primary" @click="showModal()">下载USDT提现未签名交易</Button></span>
        </p>
        <p slot="title" style="height: 40px;line-height: 35px">
          <span ><Button  style="width:200px;" type="primary" @click="goErc20Download()">下载ERC20未签名交易</Button></span>
        </p>
        <p slot="title" style="height: 40px;line-height: 35px">
          <span ><Button  style="width:200px;" type="primary" @click="showErc20Modal()">下载ERC20提现未签名交易</Button></span>
        </p>
        <p slot="title" style="height: 40px;line-height: 35px">
          <span ><Button  style="width:200px;" type="primary" @click="showSendModal()">广播ERC20交易</Button></span>
        </p>
			</Card>
		</Row>
    <Modal v-model="tixianModal" title="下载提现USDT未签名交易" :mask-closable="false">
      <Form ref="tixianForm" :model="tixianForm" :rules="ruleValidate" :label-width="80">
        <FormItem label="提现地址" prop="withdrawAddress">
          <Input v-model="tixianForm.withdrawAddress" placeholder="提现地址" style="width: 300px"></Input>
        </FormItem>
        <FormItem label="金额" prop="amount1">
          <Input v-model="tixianForm.amount1" placeholder="金额" style="width: 300px" number></Input>
        </FormItem>
      </Form>
      <div slot="footer">
        <Button type="primary" @click="goTixian()">确定</Button>
        <Button type="ghost" @click="tixianModal=false" style="margin-left: 8px">取消</Button>
      </div>
    </Modal>
    <Modal v-model="erc20Modal" title="下载提现ERC20未签名交易" :mask-closable="false">
      <Form ref="erc20Form" :model="erc20Form" :rules="ruleValidate" :label-width="80">
        <FormItem label="提现地址" prop="withdrawAddress">
          <Input v-model="erc20Form.withdrawAddress" placeholder="提现地址" style="width: 300px"></Input>
        </FormItem>
        <FormItem label="金额" prop="amount1">
          <Input v-model="erc20Form.amount1" placeholder="金额" style="width: 300px" number></Input>
        </FormItem>
        <FormItem label="币种简称" prop="amount1">
          <span>
            <Select  v-model="erc20Form.currencyName" placeholder="币种简称" style="width: 120px">
              <Option v-for="item in currencyList" :value="item" :key="item">{{ item }}</Option>
            </Select>
          </span>
        </FormItem>
      </Form>
      <div slot="footer">
        <Button type="primary" @click="goErc20Tixian()">确定</Button>
        <Button type="ghost" @click="erc20Modal=false" style="margin-left: 8px">取消</Button>
      </div>
    </Modal>
    <Modal v-model="sendModal" title="广播ERC20交易" :mask-closable="false">
      <Form ref="sendForm" :model="sendForm" :rules="ruleValidate" :label-width="80">
        <Upload
          :before-upload="handleUpload"
          action="">
          <Button type="ghost" icon="ios-cloud-upload-outline">选择要上传文件的文件</Button>
        </Upload>
        <div v-if="file !== null">需要上传的文件: {{ file.name }} </div>
      </Form>
      <div slot="footer">
        <Button type="primary" @click="sendTransaction()">确定</Button>
        <Button type="ghost" @click="sendModal=false" style="margin-left: 8px">取消</Button>
      </div>
    </Modal>

    <Row class="kkk" v-show="loading">
      <Spin  fix>
        <Icon type="load-c" size=18 class="demo-spin-icon-load"></Icon>
        <div>Loading</div>
      </Spin>
    </Row>

    <div class="dv2" v-show="loading"></div>
	</div>
</template>

<script>
	import Row from "../../../node_modules/iview/src/components/grid/row.vue";
	import { download, downloadTixian, downloadErc20, downloadErc20Tixian, currencyERC20List } from '../../api/downloadTran';
  import {baseUrl} from '../../api/baseUrl'
	export default {
		components: {
			'Row': Row
		},
		data() {
			return {
        tixianModal:false,
        erc20Modal:false,
        sendModal:false,
        loading:false,

        tixianForm: {},
        erc20Form:{},
        sendForm:{},
        currencyList:[],
				//table
				columns: [],

        file:null,

        ruleValidate: {
          withdrawAddress: [{
            required: true,
            type: 'string',
            message: '请输入地址',
            trigger: 'blur'
          }],
          amount1: [{
            required: true,
            type: 'number',
            message: '请输入金额',
            trigger: 'blur'
          }]
        }
			}
		},
		methods: {
      //下载USDT未签名交易
      goDownload() {
        this.$Modal.confirm({
          title: '确认操作',
          content: '确认下载USDT未签名交易吗？',
          onOk: () => {
            let para = {}
            this.loading=true;
            download(para).then(response => {
              this.download(response)
              this.loading=false;
            }).catch((error) => {
              this.$Message.error("下载失败:" + error.message);
              this.loading=false;
            })
          }
        });
      },
      // 下载文件
      download (response) {
        if (!response) {
          return
        }
        if (response.data.type == "application/json") {
          this.$Message.error("暂无未处理交易");
          return
        }
        let url = window.URL.createObjectURL(new Blob([response.data]))
        let link = document.createElement('a')
        link.style.display = 'none'
        link.href = url
        var fileName = response.headers["content-disposition"].split(";")[1].split("filename=")[1];
        link.setAttribute('download', fileName)
        document.body.appendChild(link)
        link.click()
      },
      //下载USDT提现未签名交易
      goTixian() {
        this.$Modal.confirm({
          title: '确认操作',
          content: '确认下载提现USDT未签名交易吗？',
          onOk: () => {
            let para = {
              withdrawAddress:this.tixianForm.withdrawAddress,
              amount:this.tixianForm.amount1
            }
            this.loading=true;
            downloadTixian(para).then(response => {
              this.download(response)
              this.loading=false;
            }).catch((error) => {
              this.$Message.error("下载失败:" + error.message);
              this.loading=false;
            })
            this.tixianModal = false;
          }
        });
      },
      //打开下载提现未签名交易界面
      showModal(){
        this.$refs['tixianForm'].resetFields();
        this.tixianModal=true;
      },
      //ERC20币种类别
      currencyERC20List () {
        let para = {}
        currencyERC20List(para).then((res) => {
          if (res.status == 200) {
            if (res.data.success == true) {
              this.currencyList = res.data.data;
            }
          }
        });
      },
      //打开下载ERC20提现未签名交易页面
      showErc20Modal(){
        this.$refs['erc20Form'].resetFields();
        this.erc20Modal=true;
      },
      //下载ERC20提现未签名交易
      goErc20Tixian() {
        this.$Modal.confirm({
          title: '确认操作',
          content: '确认下载ERC20提现未签名交易吗？',
          onOk: () => {
            this.loading=true;
            let para = {
              withdrawAddress:this.erc20Form.withdrawAddress,
              ethAmount:this.erc20Form.amount1,
              currencyName:this.erc20Form.currencyName
            }
            downloadErc20Tixian(para).then(response => {
              this.download(response)
              this.loading=false;
            }).catch((error) => {
              this.$Message.error("下载失败:" + error.message);
              this.loading=false;
            })
            this.erc20Modal=false;
          }
        });
      },
      //下载ERC20未签名交易
      goErc20Download() {
        this.$Modal.confirm({
          title: '确认操作',
          content: '确认下载ERC02未签名交易吗？',
          onOk: () => {
            this.loading=true;
            let para = {}
            downloadErc20(para).then(response => {
              this.download(response)
              this.loading=false;
            }).catch((error) => {
              this.$Message.error("下载失败:" + error.message);
              this.loading=false;
            })
          }
        });
      },
      //打开上传文件页面
      showSendModal(){
        this.$refs['sendForm'].resetFields();
        this.sendModal=true;
      },
      handleUpload (file) {
        this.file = file;
        return false;
      },
      //广播交易
      sendTransaction() {
        let _this = this;
        if (_this.file == null) {
          _this.$Message.error("文件未选择");
          return false;
        }

        let formData = new FormData();
        formData.append('file', this.file);

        let config = {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        }

        let base = baseUrl;
        let uploadUrl = base + "/otc/otc-rest/m/ethSendRawTransaction";
        _this.loading=true;
        this.$http.post(uploadUrl, formData, config).then(function (response) {
          _this.download(response)
          _this.loading=false;
        }).catch((error) => {
          _this.$Message.error("广播交易失败:" + error.message);
          _this.loading=false;
        })
        this.sendModal=false;
      }
    },
    mounted() {
      this.currencyERC20List();
    }
	};
</script>
<style>
  .demo-spin-icon-load{
    animation: ani-demo-spin 1s linear infinite;
  }
  @keyframes ani-demo-spin {
    from { transform: rotate(0deg);}
    50%  { transform: rotate(180deg);}
    to   { transform: rotate(360deg);}
  }
  .demo-spin-col{
    height: 100px;
    position: relative;
    border: 1px solid #eee;
  }
  .kkk{
    position:relative!important;
    width: 200px!important;
    left: 50%!important;
    margin-left: -100px!important;
    top: 50px!important;
    z-index: 1000001!important;
    /*opacity:0.7!important;*/
    background:none!important;
    /*background-color: rgba(0,0,0,.7)!important;*/
  }
  .kkk div{
    border: 0!important;
  }
  .dv2{
    position: absolute;
    top: 0px;
    left: 0px;
    z-index: 1000000;
    background: #fff;
    height: 100%;
    width: 100%;
    background-color: rgba(0,0,0,.7);
  }
</style>
