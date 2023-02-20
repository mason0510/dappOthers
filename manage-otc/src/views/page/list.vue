<style lang="less">
    @import '../../styles/common.less';
    @import '../../styles/table.less';
</style>


<template>

    <Card>
        <p slot="title" style="height: 35px;line-height: 35px">
            <span ><Button @click="handleAdd" :loading="loading" icon="ios-checkmark" style="width:90px;" type="primary">发布</Button></span>

        </p>
        <!--<Row>-->
            <!--<Input v-model="searchTxt" icon="search" @on-change="handleSearch" placeholder="请输入姓名搜索..." style="width: 200px" />-->
        <!--</Row>-->
        <!--<Row class="margin-top-10 searchable-table-con1">-->
            <!--<Table ref="selection" @on-selection-change="handleChangeed" :columns="columnsList" :data="columnsList"></Table>-->
            <!--<Button @click="handleSelectAll(true)">设置全选</Button>-->
            <!--<Button @click="handleSelectAll(false)">取消全选</Button>-->
        <!--</Row>-->
    </Card>

</template>

<script>
//    const editButton = (vm, h, currentRow, index) => {
//        return h('Button', {
//            props: {
//                type: currentRow.editting ? 'success' : (currentRow.saveFail ? 'error' : 'primary'),
//                loading: currentRow.saving
//            },
//            style: {
//                margin: '0 5px'
//            },
//            on: {
//                'click': () => {
//                    if (currentRow.saveFail) {
//                        currentRow.saving = vm.edittingStore[index].saving = true;
//                        vm.saveEdit(vm.editIndex(index), vm.successSave(currentRow, vm, index), vm.failSave(currentRow, vm, index));
//                    } else {
//                        if (!currentRow.editting) {
//                            if (currentRow.edittingCell) {
//                                for (let name in currentRow.edittingCell) {
//                                    currentRow.edittingCell[name] = false;
//                                    vm.edittingStore[index].edittingCell[name] = false;
//                                }
//                            }
//                            currentRow.editting = vm.edittingStore[index].editting = true;
//                        } else {
//                            currentRow.saving = vm.edittingStore[index].saving = true;
//                            vm.saveEdit(vm.editIndex(index), vm.successSave(currentRow, vm, index), vm.failSave(currentRow, vm, index));
//                        }
//                    }
//                    vm.thisTableData = JSON.parse(JSON.stringify(vm.edittingStore));
//                }
//            }
//        }, currentRow.editting ? '保存' : (currentRow.saveFail ? '重试' : '编辑'));
//    };
//    const deleteButton = (vm, h, currentRow, index) => {
//        return h('Poptip', {
//            props: {
//                confirm: true,
//                title: '您确定要删除这条数据吗?',
//                transfer: true
//            },
//            on: {
//                'on-ok': () => {
//                    currentRow.isDeleting = true;
//                    vm.deleteRow(vm.deleteIndex(index), vm.successDel(vm, index), vm.failDel(vm, index));
//                }
//            }
//        }, [
//            h('Button', {
//                style: {
//                    margin: '0 5px'
//                },
//                props: {
//                    type: 'error',
//                    placement: 'top',
//                    loading: currentRow.isDeleting
//                }
//            }, '删除')
//        ]);
//    };

    export default {
        name: 'listVue',
        props: {
            refs: String,
            columnsList: Array,
            value: Array,
            url: String,
            saveEdit: {
                type: Function,
                default () {
                    return () => {};
                }
            },
            editRow: {
                type: Function,
                default () {
                    return () => {};
                }
            },
            deleteRow: {
                type: Function,
                default () {
                    return () => {};
                }
            },
            editIncell: {
                type: Boolean,
                default: false
            },
            hoverShow: {
                type: Boolean,
                default: false
            }
        },
        data () {
            return {
                columns: [],
                thisTableData: []
            };
        },
        created () {
            this.init();
        },
        methods: {
            init () {
                let vm = this;

                this.columnsList.forEach(item => {
                    if (item.handle) {
                        item.render = (h, param) => {
                            let currentRowData = this.thisTableData[param.index];
                            if (item.handle.length === 2) {
                                return h('div', [
                                    editButton(this, h, currentRowData, param.index),
                                    deleteButton(this, h, currentRowData, param.index)
                                ]);
                            } else if (item.handle.length === 1) {
                                if (item.handle[0] === 'edit') {
                                    return h('div', [
                                        editButton(this, h, currentRowData, param.index)
                                    ]);
                                } else {
                                    return h('div', [
                                        deleteButton(this, h, currentRowData, param.index)
                                    ]);
                                }
                            }
                        };
                    }
                });
            }
        },
        watch: {
            value (data) {
                this.init();
            }
        }
    };
</script>
