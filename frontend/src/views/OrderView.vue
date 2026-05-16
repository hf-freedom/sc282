<template>
  <div class="orders">
    <el-card shadow="hover">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>订单管理</span>
          <el-button type="primary" @click="showCreateDialog">新增订单</el-button>
        </div>
      </template>
      
      <el-table :data="orders" border>
        <el-table-column type="expand">
          <template #default="{ row }">
            <div style="padding: 10px; background: #f5f7fa;">
              <h4 style="margin-bottom: 10px;">订单项详情</h4>
              <el-table :data="row.items" size="small">
                <el-table-column prop="productName" label="产品名称" />
                <el-table-column prop="quantity" label="订单数量" />
                <el-table-column prop="lockedQuantity" label="锁定数量" />
                <el-table-column prop="shippedQuantity" label="已发货数量" />
              </el-table>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="userName" label="客户姓名" />
        <el-table-column prop="address" label="地址" />
        <el-table-column prop="region" label="区域" />
        <el-table-column prop="priority" label="优先级" />
        <el-table-column prop="totalQuantity" label="总数量" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="getOrderStatusType(row.status)">{{ getOrderStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="300">
          <template #default="{ row }">
            <el-button v-if="row.status === 'LOCKED'" 
                       size="small" type="warning" @click="showPartialShipDialog(row)">
              部分发货
            </el-button>
            <el-button v-if="row.status === 'LOCKED'" 
                       size="small" type="danger" @click="showDelayDialog(row)">
              延期确认
            </el-button>
            <el-button v-if="row.status !== 'CANCELLED' && row.status !== 'COMPLETED'" 
                       size="small" type="danger" @click="showCancelDialog(row)">
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="createDialogVisible" title="新增订单" width="600px">
      <el-form :model="orderForm" label-width="100px">
        <el-form-item label="用户ID">
          <el-input v-model="orderForm.userId" />
        </el-form-item>
        <el-form-item label="用户姓名">
          <el-input v-model="orderForm.userName" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="orderForm.address" />
        </el-form-item>
        <el-form-item label="区域">
          <el-select v-model="orderForm.region" style="width: 100%;">
            <el-option label="东区" value="东区" />
            <el-option label="西区" value="西区" />
            <el-option label="南区" value="南区" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="orderForm.priority" style="width: 100%;">
            <el-option :label="1" :value="1" />
            <el-option :label="2" :value="2" />
            <el-option :label="3" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="订单项">
          <div v-for="(item, index) in orderForm.items" :key="index" style="display: flex; gap: 10px; margin-bottom: 10px;">
            <el-select v-model="item.batchId" placeholder="选择批次" style="flex: 1;">
              <el-option v-for="batch in availableBatches" :key="batch.id" :label="batch.productName" :value="batch.id" />
            </el-select>
            <el-input-number v-model="item.quantity" :min="1" placeholder="数量" />
            <el-button type="danger" @click="removeItem(index)">删除</el-button>
          </div>
          <el-button type="primary" size="small" @click="addItem">添加商品</el-button>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="createOrder">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="partialShipDialogVisible" title="确认部分发货" width="500px">
      <el-alert
        title="确认将订单标记为部分发货状态？"
        type="warning"
        :closable="false"
        style="margin-bottom: 20px;"
      />
      <el-form label-width="80px">
        <el-form-item label="备注">
          <el-input v-model="partialShipRemark" type="textarea" :rows="3" placeholder="请输入备注信息（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="partialShipDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmPartialShip">确认部分发货</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="delayDialogVisible" title="确认延期" width="500px">
      <el-alert
        title="确认将订单标记为延期状态？"
        type="error"
        :closable="false"
        style="margin-bottom: 20px;"
      />
      <el-form label-width="80px">
        <el-form-item label="备注">
          <el-input v-model="delayRemark" type="textarea" :rows="3" placeholder="请输入备注信息（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="delayDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmDelay">确认延期</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="cancelDialogVisible" title="确认取消订单" width="500px">
      <el-alert
        title="取消订单将释放已锁定的产量并重新计算相关采摘任务，确定要取消吗？"
        type="warning"
        :closable="false"
        style="margin-bottom: 20px;"
      />
      <template #footer>
        <el-button @click="cancelDialogVisible = false">返回</el-button>
        <el-button type="danger" @click="confirmCancelOrder">确认取消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { orderApi, batchApi } from '@/api'

export default {
  name: 'OrderView',
  data() {
    return {
      orders: [],
      availableBatches: [],
      createDialogVisible: false,
      partialShipDialogVisible: false,
      delayDialogVisible: false,
      cancelDialogVisible: false,
      currentOrderId: null,
      partialShipRemark: '',
      delayRemark: '',
      orderForm: {
        userId: '',
        userName: '',
        address: '',
        region: '东区',
        priority: 1,
        items: [{ batchId: '', quantity: 10 }]
      }
    }
  },
  mounted() {
    this.loadOrders()
    this.loadBatches()
  },
  methods: {
    async loadOrders() {
      const res = await orderApi.getAll()
      this.orders = res.data || []
    },
    async loadBatches() {
      const res = await batchApi.getAll()
      this.availableBatches = (res.data || []).filter(b => b.availableQuantity > 0)
    },
    showCreateDialog() {
      this.createDialogVisible = true
    },
    addItem() {
      this.orderForm.items.push({ batchId: '', quantity: 10 })
    },
    removeItem(index) {
      this.orderForm.items.splice(index, 1)
    },
    async createOrder() {
      try {
        await orderApi.create(this.orderForm)
        this.createDialogVisible = false
        this.loadOrders()
        this.$message.success('创建成功')
      } catch (e) {
        this.$message.error('创建失败: ' + (e.response?.data?.message || e.message))
      }
    },
    async cancelOrder(id) {
      await orderApi.cancel(id)
      this.loadOrders()
      this.$message.success('取消成功')
    },
    showPartialShipDialog(row) {
      this.currentOrderId = row.id
      this.partialShipRemark = row.remark || ''
      this.partialShipDialogVisible = true
    },
    async confirmPartialShip() {
      await orderApi.confirmPartialShip(this.currentOrderId, this.partialShipRemark)
      this.partialShipDialogVisible = false
      this.loadOrders()
      this.$message.success('已确认部分发货')
    },
    showDelayDialog(row) {
      this.currentOrderId = row.id
      this.delayRemark = row.remark || ''
      this.delayDialogVisible = true
    },
    showCancelDialog(row) {
      this.currentOrderId = row.id
      this.cancelDialogVisible = true
    },
    async confirmCancelOrder() {
      await orderApi.cancel(this.currentOrderId)
      this.cancelDialogVisible = false
      this.loadOrders()
      this.$message.success('订单已取消，产量已释放，采摘任务已重新计算')
    },
    async confirmDelay() {
      await orderApi.confirmDelay(this.currentOrderId, this.delayRemark)
      this.delayDialogVisible = false
      this.loadOrders()
      this.$message.success('已确认延期')
    },
    getOrderStatusType(status) {
      const map = { 'PENDING': 'info', 'LOCKED': 'primary', 'PARTIAL_SHIPPED': 'warning', 'SHIPPED': '', 'DELAYED': 'danger', 'CANCELLED': 'info', 'COMPLETED': 'success' }
      return map[status] || ''
    },
    getOrderStatusText(status) {
      const map = { 'PENDING': '待处理', 'LOCKED': '已锁定', 'PARTIAL_SHIPPED': '部分发货', 'SHIPPED': '已发货', 'DELAYED': '已延期', 'CANCELLED': '已取消', 'COMPLETED': '已完成' }
      return map[status] || status
    }
  }
}
</script>