<style lang="less">
  @import './table.less';
</style>

<template>
  <Row class="margin-top-10 ">


    <Table border ref="selection"
           @on-selection-change="selectionChange"
           :loading="loading"
           :columns="columns"
           :data="tableData">

    </Table>

    <div style="margin-top: 10px;overflow: hidden">
      <div style="float: left"  v-show="showOP">
        <Button @click="handleSelectAll(true)">设置全选</Button>
        <Button @click="handleSelectAll(false)">取消全选</Button>
      </div>
      <div style="float: right;">
        <Page :total="total" :page-size="pageSize" show-sizer show-total
              @on-change="changePage"
              @on-page-size-change="pageSizeChange"></Page>
      </div>
    </div>



  </Row>
</template>


<script>
  const otherButton = (vm, h, currentRow,index,e) => {
    return h('Button', {
      props: {
      	 type: 'ghost',
        size: 'small'
      },
      style: {
        margin: '0 5px'
      },
      on: {
        'click': () => {
          vm.doOther(currentRow,index,e);
        }
      }
    }, e.text);
  };
  const editButton = (vm, h, currentRow, index) => {
    return h('Button', {
      props: {
        type: 'primary',
        size: 'small'
      },
      style: {
        margin: '0 5px'
      },
      on: {
        'click': () => {
          vm.editRow(currentRow,index);
        }
      }
    }, '编辑');
  };
  const viewButton = (vm, h, currentRow, index) => {

    return h('Button', {
      props: {
        size: 'small'
      },
      style: {
        margin: '0 5px'
      },
      on: {
        'click': () => {
          vm.viewRow(currentRow,index);
        }
      }
    }, vm.viewText);
  };
  const deleteButton = (vm, h, currentRow, index) => {
    return h('Poptip', {
        props: {
          confirm: true,
          title: '您确定要删除这条数据吗?',
          transfer: true
        },
        on: {
          'on-ok': () => {
            vm.deleteRow(currentRow,index);
          }
        }
      }, [
        h('Button', {
          style: {
            margin: '0 5px'
          },
          props: {
            type: 'error',
            size: 'small',
            placement: 'bottom'

          }
        }, '删除')
    ])
  };

  export default {
    props: {
      columns : Array,
      tableData : Array,
      loading:Boolean,
      showOP:{
        default:true
      },
      total : Number,
      pageSize : Number,
      viewText:{
        default:'查看'
      }

    },
    data () {
      return {

      };
    },
    methods:{
      //初始化页面
      init(){
        let vm = this;

        let data = this.tableData;



        this.columns.forEach(item => {

          if (item.handle) {
            item.render = (h, param) => {


              let currentRowData = param.row;

              let buttons = [];

              item.handle.forEach(button=>{
                if(typeof button=='string'){
                  if(button.toLowerCase()=='edit'){
                    buttons.push(editButton(this, h, currentRowData, param.index));
                  }
                  else if(button.toLowerCase()=='delete'){
                    buttons.push(deleteButton(this, h, currentRowData, param.index));
                  }
                  else if(button.toLowerCase()=='view'){

                    buttons.push(viewButton(this, h, currentRowData, param.index));
                  }
                }else{
                  let btnItem = button;
                  buttons.push(otherButton(this, h, currentRowData, param.index,btnItem));
                }



              });

              return h('div', buttons);

            };
          }
        });
      },
      // 表格行选中变化
      selectionChange(selection){
        this.$emit('selectionChange',selection)
      },
      // 表格编辑按钮
      editRow(row,index){
        this.$emit('editRow',row,index);
      },
      // 表格删除按钮
      deleteRow(row,index){
        this.$emit('deleteRow',row,index);
      },
      // 表格查看按钮
      viewRow(row,index){
        this.$emit('viewRow',row,index);
      },
      // 表格行全选
      handleSelectAll (status) {
        this.$refs.selection.selectAll(status);
      },
      // 表格分页
      changePage (index) {
        this.$emit('changePage',index);
      },
      // 表格每页数量切换
      pageSizeChange(pagesize){
        this.$emit('pageSizeChange',pagesize);
      },
      //其它按钮点击事件
      doOther(currentRow,index,e){
        // type=1 图片展示
        this.$emit('doOther',currentRow,index,e);
      }
    },
    watch: {
      tableData (data) {
//        alert(1);
//        alert(JSON.stringify(this.tableData));
        this.init();
      }
    },
    mounted () {
      this.init();
    }
  }
</script>
<style scoped="">

</style>
