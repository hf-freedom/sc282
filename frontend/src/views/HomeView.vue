<template>
  <div class="home">
    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="text-align: center;">
            <div style="font-size: 48px; color: #3498db;">{{ batchCount }}</div>
            <div style="font-size: 16px; color: #666;">总批次</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="text-align: center;">
            <div style="font-size: 48px; color: #e74c3c;">{{ orderCount }}</div>
            <div style="font-size: 16px; color: #666;">总订单</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="text-align: center;">
            <div style="font-size: 48px; color: #f39c12;">{{ taskCount }}</div>
            <div style="font-size: 16px; color: #666;">采摘任务</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="text-align: center;">
            <div style="font-size: 48px; color: #27ae60;">{{ vehicleCount }}</div>
            <div style="font-size: 16px; color: #666;">配送车辆</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <span>最近批次</span>
              <el-button type="primary" size="small" @click="$router.push('/batches')">查看全部</el-button>
            </div>
          </template>
          <el-table :data="recentBatches" size="small">
            <el-table-column prop="productName" label="产品" />
            <el-table-column prop="farmerName" label="农户" />
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <span>最近订单</span>
              <el-button type="primary" size="small" @click="$router.push('/orders')">查看全部</el-button>
            </div>
          </template>
          <el-table :data="recentOrders" size="small">
            <el-table-column prop="userName" label="客户" />
            <el-table-column prop="region" label="区域" />
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <el-tag :type="getOrderStatusType(row.status)">{{ getOrderStatusText(row.status) }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { batchApi, orderApi, taskApi, deliveryApi } from '@/api'

export default {
  name: 'HomeView',
  data() {
    return {
      batchCount: 0,
      orderCount: 0,
      taskCount: 0,
      vehicleCount: 0,
      recentBatches: [],
      recentOrders: []
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    async loadData() {
      const [batches, orders, tasks, vehicles] = await Promise.all([
        batchApi.getAll(),
        orderApi.getAll(),
        taskApi.getAll(),
        deliveryApi.getVehicles()
      ])
      
      this.batchCount = batches.data ? batches.data.length : 0
      this.orderCount = orders.data ? orders.data.length : 0
      this.taskCount = tasks.data ? tasks.data.length : 0
      this.vehicleCount = vehicles.data ? vehicles.data.length : 0
      
      this.recentBatches = (batches.data || []).slice(0, 5)
      this.recentOrders = (orders.data || []).slice(0, 5)
    },
    getStatusType(status) {
      const map = { 'PENDING': 'info', 'MATURE': 'success', 'HARVESTING': 'warning', 'HARVESTED': '', 'EXPIRED': 'danger' }
      return map[status] || ''
    },
    getStatusText(status) {
      const map = { 'PENDING': '待成熟', 'MATURE': '已成熟', 'HARVESTING': '采摘中', 'HARVESTED': '已采摘', 'EXPIRED': '已过期' }
      return map[status] || status
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

<style scoped>
.home {
  padding: 0;
}
</style>