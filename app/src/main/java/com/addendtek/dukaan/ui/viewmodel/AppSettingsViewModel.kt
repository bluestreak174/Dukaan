package com.addendtek.dukaan.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.addendtek.dukaan.data.entities.Category
import com.addendtek.dukaan.data.entities.Product
import com.addendtek.dukaan.data.entities.PurchaseBill
import com.addendtek.dukaan.data.entities.Purchases
import com.addendtek.dukaan.data.entities.QuantityType
import com.addendtek.dukaan.data.entities.Sales
import com.addendtek.dukaan.data.entities.SalesBill
import com.addendtek.dukaan.data.repositories.CategoryRepository
import com.addendtek.dukaan.data.repositories.ProductRepository
import com.addendtek.dukaan.data.repositories.PurchaseBillRepository
import com.addendtek.dukaan.data.repositories.PurchaseRepository
import com.addendtek.dukaan.data.repositories.QuantityTypeRepository
import com.addendtek.dukaan.data.repositories.SalesBillRepository
import com.addendtek.dukaan.data.repositories.SalesRepository
import com.addendtek.dukaan.data.repositories.UserPreferencesRepository
import com.opencsv.CSVReader
import com.opencsv.CSVWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class AppSettingsViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val quantityTypeRepository: QuantityTypeRepository,
    private val purchaseBillRepository: PurchaseBillRepository,
    private val salesBillRepository: SalesBillRepository,
    private val salesRepository: SalesRepository,
    private val purchaseRepository: PurchaseRepository

) : ViewModel() {
    var productList: List<Product> = listOf()
    var categoryList: List<Category> = listOf()
    var qtyTypeList: List<QuantityType> = listOf()
    var purchaseList: List<Purchases> = listOf()
    var salesList: List<Sales> = listOf()
    var purchaseBillsList: List<PurchaseBill> = listOf()
    var salesBillList: List<SalesBill> = listOf()
    var appNameUIState by mutableStateOf(AppNameUIState())
        private set

    var exportImportSettingsState  by mutableStateOf(ExportImportSettingsState())
        private set

    val appNamePrefState: StateFlow<AppNameUIState> =
        userPreferencesRepository.appName.map { appName ->
            AppNameUIState(appName = appName, isEntryValid = appName.isNotBlank())
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = AppNameUIState()
            )

    val appHelpPrefState: StateFlow<AppHelpPrefState> =
        userPreferencesRepository.appHelpEnabled.map { appHelpEnabled ->
            AppHelpPrefState(isAppHelpEnabled = appHelpEnabled)
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = AppHelpPrefState()
            )
    //private val _appHelpPrefState = MutableStateFlow(AppHelpPrefState())

    //var appHelpPrefState: StateFlow<AppHelpPrefState> = _appHelpPrefState.asStateFlow()

    init {
        //loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            userPreferencesRepository.appHelpEnabled.map { appHelpEnabled ->
                //_appHelpPrefState.value = _appHelpPrefState.value.copy(isAppHelpEnabled = appHelpEnabled)
            }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = AppHelpPrefState()
                )
        }
    }

    fun saveAppName() {
        viewModelScope.launch {
            userPreferencesRepository.saveAppNamePreference(appNameUIState.appName)
        }
    }

    fun saveAppHelpState(isAppHelpEnabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.saveAppHelpPreference(isAppHelpEnabled)
        }
    }


    fun updateUiState(appName: String) {
        val isEntryValid = appName.isNotEmpty()
        appNameUIState=AppNameUIState(appName = appName, isEntryValid = isEntryValid)
    }

    fun updateExportImport(type: String){
        exportImportSettingsState = ExportImportSettingsState(
            selectedOption = type,
            tableNames = exportImportSettingsState.tableNames
        )
    }

    fun updateTablesSelection(tableName: String){
        exportImportSettingsState = ExportImportSettingsState(
            selectedOption = exportImportSettingsState.selectedOption,
            tableNames = exportImportSettingsState.tableNames,
            selectedTable = tableName
        )
        viewModelScope.launch {
            when(tableName) {
                "Products" -> getProducts()
                "Categories" -> getCategories()
                "QtyTypes" -> getQtyTypes()
                "Purchases" -> getPurchases()
                "Sales" -> getSales()
                "Purchase Bills" -> getPurchaseBills()
                "Sales Bills" -> getSalesBills()
            }
        }

    }

    suspend fun  exportCSV(context: Context ,  uri: Uri): String{
        var successMsg = "ERROR"
        //export selected tables to csv
        // Call the suspend function
         successMsg = exportRoomDataToCsv(context, exportImportSettingsState.selectedTable, uri)
        // Show success message or handle post-export logic
        return successMsg


    }

    suspend fun importCSV(context: Context, uri: Uri): String{
        var successMsg = "ERROR"
        successMsg = importCsvData(context, exportImportSettingsState.selectedTable, uri)
        return successMsg
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }


    private suspend fun exportRoomDataToCsv(context: Context, tableName: String, uri: Uri?): String {
        var successMsg = "FAILED, URI IS NULL"
        if (uri == null) return successMsg

        withContext(Dispatchers.IO) {
            try {
                // Use ContentResolver to open an output stream to the provided Uri
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    OutputStreamWriter(outputStream).use { writer ->
                        CSVWriter(writer).use { csvWriter ->
                            when(tableName) {
                                "Products" -> exportProducts(csvWriter)
                                "Categories" -> exportCategories(csvWriter)
                                "QtyTypes" -> exportQtyTypes(csvWriter)
                                "Purchases" -> exportPurchases(csvWriter)
                                "Sales" -> exportSales(csvWriter)
                                "Purchase Bills" -> exportPurchaseBills(csvWriter)
                                "Sales Bills" -> exportSalesBills(csvWriter)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle error (e.g., show a Toast or log the error)
                successMsg = "FAILED, ERROR IN EXPORT"
            }
        }
        successMsg = "SUCCESS"
        return successMsg
    }

    suspend fun importCsvData(context: Context, tableName: String, uri: Uri? ): String {
        var successMsg = "FAILED, URI IS NULL"
        if (uri == null) return successMsg
        withContext(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val reader = CSVReader(InputStreamReader(inputStream))
                // Skip the header line if present
                reader.readNext()

                when(tableName) {
                    "Products" -> importProducts(reader)
                    "Categories" -> importCategories(reader)
                    "QtyTypes" -> importQtyTypes(reader)
                    "Purchases" -> importPurchases(reader)
                    "Sales" -> importSales(reader)
                    "Purchase Bills" -> importPurchaseBill(reader)
                    "Sales Bills" -> importSalesBill(reader)
                }

                reader.close()


                // Handle success (e.g., log it)
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle error
                successMsg = "FAILED, ERROR IN IMPORT"
            }
        }
        successMsg = "SUCCESS"
        return successMsg
    }

    suspend fun importProducts(csvReader: CSVReader){
        val entities = mutableListOf<Product>()
        var nextLine: Array<String>?
        while (csvReader.readNext().also { nextLine = it } != null) {
            // Assuming the CSV columns match the entity fields in order
            val entity = Product(
                id = nextLine!![0].toInt(),
                name = nextLine!![1],
                categoryId = nextLine!![2].toInt(),
                mrp = nextLine!![3].toDouble(),
                cost = nextLine!![4].toDouble(),
                qty = nextLine!![5].toInt(),
                qtyTypeId =  nextLine!![6].toInt(),
                barCode =  nextLine!![7].toLong()
            )
            entities.add(entity)
        }
        for(product in entities){
            productRepository.insertProduct(product)
        }

    }

    suspend fun importCategories(csvReader: CSVReader){
        val entities = mutableListOf<Category>()
        var nextLine: Array<String>?
        while (csvReader.readNext().also { nextLine = it } != null) {
            // Assuming the CSV columns match the entity fields in order
            val entity = Category(
                id = nextLine!![0].toInt(),
                name = nextLine!![1],
            )
            entities.add(entity)
        }

        for(category in entities){
            categoryRepository.upsertCategory(category)
        }
    }

    suspend fun importQtyTypes(csvReader: CSVReader){
        val entities = mutableListOf<QuantityType>()
        var nextLine: Array<String>?
        while (csvReader.readNext().also { nextLine = it } != null) {
            // Assuming the CSV columns match the entity fields in order
            val entity = QuantityType(
                id = nextLine!![0].toInt(),
                type = nextLine!![1],
                piece = nextLine!![2].toInt()
            )
            entities.add(entity)
        }

        for(qtyType in entities){
            quantityTypeRepository.upsertQuantityType(qtyType)
        }

    }

    suspend fun importPurchases(csvReader: CSVReader){
        val entities = mutableListOf<Purchases>()
        var nextLine: Array<String>?
        while (csvReader.readNext().also { nextLine = it } != null) {
            // Assuming the CSV columns match the entity fields in order
            val entity = Purchases(
                id = nextLine!![0].toInt(),
                billId = nextLine!![1].toInt(),
                purchaseDate = nextLine!![2].toLong(),
                productId = nextLine!![3].toInt(),
                categoryId = nextLine!![4].toInt(),
                quantityTypeId = nextLine!![5].toInt(),
                quantity = nextLine!![6].toInt(),
                cost = nextLine!![7].toDouble(),
                cash = nextLine!![8].toDouble(),
                upi = nextLine!![9].toDouble()
            )
            entities.add(entity)
        }

        for(purchase in entities){
            purchaseRepository.upsertPurchases(purchase)
        }
    }

    suspend fun importSales(csvReader: CSVReader){
        val entities = mutableListOf<Sales>()
        var nextLine: Array<String>?
        while (csvReader.readNext().also { nextLine = it } != null) {
            // Assuming the CSV columns match the entity fields in order
            val entity = Sales(
                id = nextLine!![0].toInt(),
                billId = nextLine!![1].toInt(),
                sellDate = nextLine!![2].toLong(),
                productId = nextLine!![3].toInt(),
                categoryId = nextLine!![4].toInt(),
                quantityTypeId = nextLine!![5].toInt(),
                quantity = nextLine!![6].toInt(),
                price = nextLine!![7].toDouble(),
                cash = nextLine!![8].toDouble(),
                upi = nextLine!![9].toDouble()
            )
            entities.add(entity)
        }

        for(sales in entities){
            salesRepository.upsertSales(sales)
        }
    }

    suspend fun importPurchaseBill(csvReader: CSVReader){
        val entities = mutableListOf<PurchaseBill>()
        var nextLine: Array<String>?
        while (csvReader.readNext().also { nextLine = it } != null) {
            // Assuming the CSV columns match the entity fields in order
            val entity = PurchaseBill(
                id = nextLine!![0].toInt(),
                billDate = nextLine!![1].toLong(),
                billAddress = nextLine!![2],
                total = nextLine!![3].toDouble(),
                cash = nextLine!![4].toDouble(),
                upi = nextLine!![5].toDouble(),
                isDraft = nextLine!![6].toBoolean()
            )
            entities.add(entity)
        }

        for(purchaseBill in entities){
            purchaseBillRepository.upsertPurchaseBill(purchaseBill)
        }
    }

    suspend fun importSalesBill(csvReader: CSVReader){
        val entities = mutableListOf<SalesBill>()
        var nextLine: Array<String>?
        while (csvReader.readNext().also { nextLine = it } != null) {
            // Assuming the CSV columns match the entity fields in order
            val entity = SalesBill(
                id = nextLine!![0].toInt(),
                billDate = nextLine!![1].toLong(),
                billAddress = nextLine!![2],
                total = nextLine!![3].toDouble(),
                cash = nextLine!![4].toDouble(),
                upi = nextLine!![5].toDouble(),
                isDraft = nextLine!![6].toBoolean()
            )
            entities.add(entity)
        }

        for(salesBill in entities){
            salesBillRepository.upsertSalesBill(salesBill)
        }
    }


    fun exportProducts(csvWriter: CSVWriter){
        // Write header row (adjust column names as needed)
        csvWriter.writeNext(arrayOf("ID", "NAME", "CATGORYID", "MRP", "COST", "QTY", "QTYTYPEID", "BARCODE"))

        // Write data rows
        productList.forEach { entity ->
            csvWriter.writeNext(arrayOf(
                entity.id.toString(),
                entity.name,
                entity.categoryId.toString(),
                entity.mrp.toString(),
                entity.cost.toString(),
                entity.qty.toString(),
                entity.qtyTypeId.toString(),
                entity.barCode.toString()
            ))
        }
    }


    fun exportCategories(csvWriter: CSVWriter){
        // Write header row (adjust column names as needed)
        csvWriter.writeNext(arrayOf("ID", "NAME"))

        // Write data rows
        categoryList.forEach { entity ->
            csvWriter.writeNext(arrayOf(
                entity.id.toString(),
                entity.name,
            ))
        }
    }

    fun exportQtyTypes(csvWriter: CSVWriter){
        // Write header row (adjust column names as needed)
        csvWriter.writeNext(arrayOf("ID", "TYPE", "PIECE"))

        // Write data rows
        qtyTypeList.forEach { entity ->
            csvWriter.writeNext(arrayOf(
                entity.id.toString(),
                entity.type,
                entity.piece.toString()
            ))
        }
    }

    fun exportPurchases(csvWriter: CSVWriter){
        // Write header row (adjust column names as needed)
        csvWriter.writeNext(arrayOf("ID", "BILLID", "DATE", "PRODUCTID", "CATEGORYID", "QTYTYPEID", "QTY", "COST", "CASH", "UPI"))

        // Write data rows
        purchaseList.forEach { entity ->
            csvWriter.writeNext(arrayOf(
                entity.id.toString(),
                entity.billId.toString(),
                entity.purchaseDate.toString(),
                entity.productId.toString(),
                entity.categoryId.toString(),
                entity.quantityTypeId.toString(),
                entity.quantity.toString(),
                entity.cost.toString(),
                entity.cash.toString(),
                entity.upi.toString()
            ))
        }
    }

    fun exportSales(csvWriter: CSVWriter){
        // Write header row (adjust column names as needed)
        csvWriter.writeNext(arrayOf("ID", "BILLID", "DATE", "PRODUCTID", "CATEGORYID", "QTYTYPEID", "QTY", "PRICE", "CASH", "UPI"))

        // Write data rows
        salesList.forEach { entity ->
            csvWriter.writeNext(arrayOf(
                entity.id.toString(),
                entity.billId.toString(),
                entity.sellDate.toString(),
                entity.productId.toString(),
                entity.categoryId.toString(),
                entity.quantityTypeId.toString(),
                entity.quantity.toString(),
                entity.price.toString(),
                entity.cash.toString(),
                entity.upi.toString()
            ))
        }
    }

    fun exportPurchaseBills(csvWriter: CSVWriter){
        // Write header row (adjust column names as needed)
        csvWriter.writeNext(arrayOf("ID", "DATE", "ADDRESS", "TOTAL", "CASH", "UPI", "ISDRAFT"))

        // Write data rows
        purchaseBillsList.forEach { entity ->
            csvWriter.writeNext(arrayOf(
                entity.id.toString(),
                entity.billDate.toString(),
                entity.billAddress,
                entity.total.toString(),
                entity.cash.toString(),
                entity.upi.toString(),
                entity.isDraft.toString()
            ))
        }
    }

    fun exportSalesBills(csvWriter: CSVWriter){
        // Write header row (adjust column names as needed)
        csvWriter.writeNext(arrayOf("ID", "DATE", "ADDRESS", "TOTAL", "CASH", "UPI", "ISDRAFT"))

        // Write data rows
        salesBillList.forEach { entity ->
            csvWriter.writeNext(arrayOf(
                entity.id.toString(),
                entity.billDate.toString(),
                entity.billAddress,
                entity.total.toString(),
                entity.cash.toString(),
                entity.upi.toString(),
                entity.isDraft.toString()
            ))
        }
    }

        suspend fun getProducts(){
            productRepository.getAllProductsStream().collect {
                productList = it
            }
        }
        suspend fun getCategories(){
            categoryRepository.getAllCategoriesStream().collect {
                categoryList = it
            }
        }
        suspend fun getQtyTypes(){
            quantityTypeRepository.getAllQuantityTypesStream().collect{
                qtyTypeList = it
            }
        }
        suspend fun getPurchases(){
            purchaseRepository.getAllPurchasesStream().collect{
                purchaseList = it
            }
        }
    suspend fun getSales(){
        salesRepository.getAllSalesStream().collect {
            salesList = it
        }
    }
    suspend fun getPurchaseBills(){
        purchaseBillRepository.getAllPurchaseBillStream().collect{
            purchaseBillsList = it
        }
    }
    suspend fun getSalesBills(){
        salesBillRepository.getAllSalesBillStream().collect{
            salesBillList = it
        }
    }








}

data class AppNameUIState(
    val appName: String = "",
    val isEntryValid: Boolean = false
)

data class ExportImportSettingsState(
    val selectedOption: String = "Export",
    val tableNames: List<String> = listOf(
        "Categories",
        "QtyTypes",
        "Products",
        "Purchases",
        "Sales",
        "Purchase Bills",
        "Sales Bills"
    ),
    val selectedTable: String = "Products"
)

data class AppHelpPrefState(
    val isAppHelpEnabled: Boolean = true
)

