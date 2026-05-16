<template>
  <div class="batches">
    <el-card shadow="hover">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>批次管理</span>
          <el-button type="primary" @click="showCreateDialog">新增批次</el-button>
        </div>
      </template>
      
      <el-table :data="batches" border>
        <el-table-column prop="productName" label="产品名称" />
        <el-table-column prop="farmerName" label="农户" />
        <el-table-column prop="region" label="区域" />
        <el-table-column prop="totalQuantity" label="总数量" />
        <el-table-column prop="availableQuantity" label="可用数量" />
        <el-table-column prop="lockedQuantity" label="已锁定" />
        <el-table-column prop="maturityLevel" label="成熟度">
          <template #default="{ row }">
            <el-progress :percentage="row.maturityLevel" :status="row.maturityLevel >= 100 ? 'success' : ''" />
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button size="small" @click="updateMaturity(row)">更新成熟度</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="createDialogVisible" title="新增批次" width="500px">
      <el-form :model="batchForm" label-width="100px">
        <el-form-item label="农户ID">
          <el-input v-model="batchForm.farmerId" />
        </el-form-item>
        <el-form-item label="农户姓名">
          <el-input v-model="batchForm.farmerName" />
        </el-form-item>
        <el-form-item label="产品名称">
          <el-input v-model="batchForm.productName" />
        </el-form-item>
        <el-form-item label="区域">
          <el-select v-model="batchForm.region" style="width: 100%;">
            <el-option label="东区" value="东区" />
            <el-option label="西区" value="西区" />
            <el-option label="南区" value="南区" />
          </el-select>
        </el-form-item>
        <el-form-item label="总数量">
          <el-input-number v-model="batchForm.totalQuantity" :min="1" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="成熟时间">
          <el-date-picker v-model="batchForm.matureTime" type="datetime" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="有效期(小时)">
          <el-input-number v-model="batchForm.expireHours" :min="1" style="width: 100%;" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="createBatch">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="maturityDialogVisible" title="更新成熟度" width="400px">
      <el-form label-width="80px">
        <el-form-item label="成熟度">
          <el-slider v-model="maturityLevel" :min="0" :max="100" />
          <div style="text-align: center;">{{ maturityLevel }}%</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="maturityDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveMaturity">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { batchApi } from '@/api'

export default {
  name: 'BatchView',
  data() {
    return {
      batches: [],
      createDialogVisible: false,
      maturityDialogVisible: false,
      currentBatchId: null,
      maturityLevel: 0,
      batchForm: {
        farmerId: '',
        farmerName: '',
        productName: '',
        region: '东区',
        totalQuantity: 100,
        matureTime: new Date(),
        expireHours: 72
      }
    }
  },
  mounted() {
    this.loadBatches()
  },
  methods: {
    async loadBatches() {
      const res = await batchApi.getAll()
      this.batches = res.data || []
    },
    showCreateDialog() {
      this.createDialogVisible = true
    },
    async createBatch() {
      await batchApi.create(this.batchForm)
      this.createDialogVisible = false
      this.loadBatches()
      this.$message.success('创建成功')
    },
    updateMaturity(row) {
      this.currentBatchId = row.id
      this.maturityLevel = row.maturityLevel
      this.maturityDialogVisible = true
    },
    async saveMaturity() {
      await batchApi.updateMaturity(this.currentBatchId, this.maturityLevel)
      this.maturityDialogVisible = false
      this.loadBatches()
      this.$message.success('更新成功')
    },
    getStatusType(status) {
      const map = { 'PENDING': 'info', 'MATURE': 'success', 'HARVESTING': 'warning', 'HARVESTED': '', 'EXPIRED': 'danger' }
      return map[status] || ''
    },
    getStatusText(status) {
      const map = { 'PENDING': '待成熟', 'MATURE': '已成熟', 'HARVESTING': '采摘中', 'HARVESTED': '已采摘', 'EXPIRED': '已过期' }
      return map[status] || status
    }
  }
}
</script>