package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Product
import com.example.viewmodel.AuthViewModel
import com.example.viewmodel.ProductViewModel
import com.example.viewmodel.UploadUiState
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainHubScreen(
    authViewModel: AuthViewModel,
    productViewModel: ProductViewModel,
    onProductClick: (String) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val currentUserEmail by authViewModel.currentUserEmail.collectAsState()

    Scaffold(
        topBar = {
            if (selectedTab > 0) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = when (selectedTab) {
                                1 -> "Sell Product"
                                else -> "My Favorites"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                authViewModel.logout()
                                onLogout()
                            },
                            modifier = Modifier.testTag("logout_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "Log Out"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            }
        },
        bottomBar = {
            Column {
                Divider(color = Color(0xFFE6E0E9), thickness = 1.dp)
                NavigationBar(
                    modifier = Modifier.navigationBarsPadding(),
                    containerColor = Color(0xFFF3EDF7)
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == 0) Icons.Filled.Home else Icons.Outlined.Home,
                                contentDescription = "Home"
                            )
                        },
                        label = { Text("Home", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF1D192B),
                            selectedTextColor = Color(0xFF1D192B),
                            unselectedIconColor = Color(0xFF49454F),
                            unselectedTextColor = Color(0xFF49454F),
                            indicatorColor = Color(0xFFE8DEF8)
                        ),
                        modifier = Modifier.testTag("nav_home")
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == 1) Icons.Filled.AddCircle else Icons.Outlined.AddCircle,
                                contentDescription = "Upload"
                            )
                        },
                        label = { Text("Upload", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF1D192B),
                            selectedTextColor = Color(0xFF1D192B),
                            unselectedIconColor = Color(0xFF49454F),
                            unselectedTextColor = Color(0xFF49454F),
                            indicatorColor = Color(0xFFE8DEF8)
                        ),
                        modifier = Modifier.testTag("nav_upload")
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == 2) Icons.Filled.Favorite else Icons.Outlined.Favorite,
                                contentDescription = "Favorites"
                            )
                        },
                        label = { Text("Favorites", fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Medium) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF1D192B),
                            selectedTextColor = Color(0xFF1D192B),
                            unselectedIconColor = Color(0xFF49454F),
                            unselectedTextColor = Color(0xFF49454F),
                            indicatorColor = Color(0xFFE8DEF8)
                        ),
                        modifier = Modifier.testTag("nav_favorites")
                    )
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> HomeScreen(
                    productViewModel = productViewModel,
                    currentUserEmail = currentUserEmail ?: "",
                    onLogout = {
                        authViewModel.logout()
                        onLogout()
                    },
                    onProductClick = onProductClick
                )
                1 -> UploadScreen(productViewModel, currentUserEmail ?: "")
                2 -> FavoritesScreen(productViewModel, onProductClick)
            }
        }
    }
}

@Composable
fun HomeScreen(
    productViewModel: ProductViewModel,
    currentUserEmail: String,
    onLogout: () -> Unit,
    onProductClick: (String) -> Unit
) {
    val recommended by productViewModel.recommendedProducts.collectAsState()
    val uploaded by productViewModel.uploadedProducts.collectAsState()
    val isLoading by productViewModel.isLoading.collectAsState()
    val errorMessage by productViewModel.errorMessage.collectAsState()

    val searchQuery by productViewModel.searchQuery.collectAsState()
    val selectedCategory by productViewModel.selectedCategory.collectAsState()

    val categories = listOf("All", "Electronics", "Jewelry", "Men's Clothing", "Women's Clothing")

    val categoryMapping = mapOf(
        "Electronics" to "electronics",
        "Jewelry" to "jewelery",
        "Men's Clothing" to "men's clothing",
        "Women's Clothing" to "women's clothing"
    )

    // Filtering logic
    val filteredUploaded = remember(uploaded, searchQuery, selectedCategory) {
        uploaded.filter { product ->
            val matchesSearch = product.title.contains(searchQuery, ignoreCase = true) ||
                    product.description.contains(searchQuery, ignoreCase = true)
            val matchesCategory = selectedCategory == "All" || 
                    product.category.equals(selectedCategory, ignoreCase = true)
            matchesSearch && matchesCategory
        }
    }

    val filteredRecommended = remember(recommended, searchQuery, selectedCategory) {
        val targetCategory = categoryMapping[selectedCategory]
        recommended.filter { product ->
            val matchesSearch = product.title.contains(searchQuery, ignoreCase = true) ||
                    product.description.contains(searchQuery, ignoreCase = true)
            val matchesCategory = selectedCategory == "All" || 
                    product.category.equals(targetCategory, ignoreCase = true)
            matchesSearch && matchesCategory
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFBFE))
    ) {
        // High Density Custom Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "TuteDude E-Store",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6750A4),
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Explore",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1C1B1F)
                )
            }
            
            // Interactive Profile Avatar representing the current logged-in user
            var showLogoutDialog by remember { mutableStateOf(false) }
            val initials = if (currentUserEmail.length >= 2) currentUserEmail.take(2).uppercase() else "JD"
            
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFEADDFF), shape = androidx.compose.foundation.shape.CircleShape)
                    .clickable { showLogoutDialog = true }
                    .testTag("avatar_profile_badge"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF21005D)
                )
            }

            if (showLogoutDialog) {
                AlertDialog(
                    onDismissRequest = { showLogoutDialog = false },
                    title = { Text("Log Out") },
                    text = { Text("Are you sure you want to log out from $currentUserEmail?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showLogoutDialog = false
                                onLogout()
                            },
                            modifier = Modifier.testTag("logout_confirm_dialog")
                        ) {
                            Text("Log Out", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showLogoutDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { productViewModel.setSearchQuery(it) },
            placeholder = { Text("Search products...", color = Color(0xFF49454F)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon", tint = Color(0xFF49454F)) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { productViewModel.setSearchQuery("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear Search", tint = Color(0xFF49454F))
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF3EDF7),
                unfocusedContainerColor = Color(0xFFF3EDF7),
                focusedBorderColor = Color(0xFFE6E0E9),
                unfocusedBorderColor = Color(0xFFE6E0E9),
                focusedTextColor = Color(0xFF1C1B1F),
                unfocusedTextColor = Color(0xFF1C1B1F),
                cursorColor = Color(0xFF6750A4)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .testTag("search_bar")
        )

        // Categories List
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { productViewModel.setCategory(category) },
                    label = { Text(category) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFE8DEF8),
                        selectedLabelColor = Color(0xFF1D192B),
                        containerColor = Color.Transparent,
                        labelColor = Color(0xFF49454F)
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selectedCategory == category,
                        borderColor = Color(0xFFE6E0E9),
                        selectedBorderColor = Color(0xFFE6E0E9)
                    ),
                    modifier = Modifier.testTag("category_chip_$category")
                )
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF6750A4))
            }
        } else if (!errorMessage.isNullOrEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = "Error icon",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = errorMessage ?: "",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { productViewModel.refreshAll() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4))) {
                    Text("Retry Connection")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // Horizontal Recommended Products Section
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "Recommended",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1C1B1F)
                        )
                        Text(
                            text = "See all",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF6750A4)
                        )
                    }
                }

                if (filteredRecommended.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F2FA)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE6E0E9))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No recommended products matching filters",
                                    fontSize = 13.sp,
                                    color = Color(0xFF49454F)
                                )
                            }
                        }
                    }
                } else {
                    item {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredRecommended) { product ->
                                RecommendedItemCard(product, onProductClick)
                            }
                        }
                    }
                }

                // Recent Products (User Uploads) Header
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recent Products",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1C1B1F)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(modifier = Modifier.size(6.dp).background(Color(0xFF6750A4), shape = androidx.compose.foundation.shape.CircleShape))
                            Box(modifier = Modifier.size(6.dp).background(Color(0xFFE6E0E9), shape = androidx.compose.foundation.shape.CircleShape))
                        }
                    }
                }

                if (filteredUploaded.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCAC4D0))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "No user-uploaded products found",
                                    fontSize = 13.sp,
                                    color = Color(0xFF49454F)
                                )
                            }
                        }
                    }
                } else {
                    // Chunk the user listings into pairs of 2 to display them in a clean 2-column grid format
                    val chunkedList = filteredUploaded.chunked(2)
                    items(chunkedList) { pair ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            pair.forEach { product ->
                                Box(modifier = Modifier.weight(1f)) {
                                    GridProductCard(product, onProductClick)
                                }
                            }
                            if (pair.size < 2) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecommendedItemCard(
    product: Product,
    onProductClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(180.dp)
            .clickable { onProductClick(product.id) }
            .testTag("product_card_${product.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F2FA)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE6E0E9))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = product.images.firstOrNull(),
                contentDescription = product.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFD0BCFF))
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = product.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color(0xFF1C1B1F)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "$${String.format("%.2f", product.price)}",
                    fontSize = 11.sp,
                    color = Color(0xFF49454F)
                )
            }
        }
    }
}

@Composable
fun GridProductCard(
    product: Product,
    onProductClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onProductClick(product.id) }
            .testTag("product_card_${product.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCAC4D0))
    ) {
        Column {
            // Product Image Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(Color(0xFFF3F0F5))
            ) {
                AsyncImage(
                    model = product.images.firstOrNull(),
                    contentDescription = product.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                
                // Favorite or Recommended indicator/badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (product.isRecommended) Color(0xFFE8DEF8) else Color(0xFFFFD8E4)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (product.isRecommended) "API" else "USER",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (product.isRecommended) Color(0xFF21005D) else Color(0xFF6750A4),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Text Content Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = product.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color(0xFF1C1B1F)
                )
                Text(
                    text = product.description,
                    fontSize = 10.sp,
                    color = Color(0xFF49454F),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 12.sp
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$${String.format("%.2f", product.price)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1C1B1F)
                    )
                    
                    // Small "New" or category tag
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFE8DEF8), shape = RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (product.isRecommended) "API" else "NEW",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF21005D)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductItemCard(
    product: Product,
    onProductClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    GridProductCard(product, onProductClick, modifier)
}

@Composable
fun UploadScreen(
    productViewModel: ProductViewModel,
    uploaderEmail: String
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priceStr by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Electronics") }

    // Minimum 3 image URLs. Prepopulate with high-quality placeholder URLs so uploads are fun and work perfectly
    var imageUrl1 by remember { mutableStateOf("https://picsum.photos/id/1/600/600") }
    var imageUrl2 by remember { mutableStateOf("https://picsum.photos/id/2/600/600") }
    var imageUrl3 by remember { mutableStateOf("https://picsum.photos/id/3/600/600") }

    var uploaderName by remember { mutableStateOf("") }
    var uploaderContact by remember { mutableStateOf(uploaderEmail) }

    val uploadState by productViewModel.uploadState.collectAsState()
    val categories = listOf("Electronics", "Jewelry", "Men's Clothing", "Women's Clothing")

    var showDropdown by remember { mutableStateOf(false) }

    LaunchedEffect(uploadState) {
        if (uploadState is UploadUiState.Success) {
            title = ""
            description = ""
            priceStr = ""
            imageUrl1 = "https://picsum.photos/id/20/600/600"
            imageUrl2 = "https://picsum.photos/id/21/600/600"
            imageUrl3 = "https://picsum.photos/id/22/600/600"
            uploaderName = ""
            productViewModel.resetUploadState()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Upload Your Product",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "List your own item for others to view",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }

            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Product Title") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("upload_title")
                )
            }

            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Product Description") },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("upload_description")
                )
            }

            item {
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = priceStr,
                        onValueChange = { priceStr = it },
                        label = { Text("Price ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("upload_price")
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Box(modifier = Modifier.weight(1.2f)) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category") },
                            trailingIcon = {
                                IconButton(onClick = { showDropdown = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Choose Category")
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("upload_category_field")
                        )
                        DropdownMenu(
                            expanded = showDropdown,
                            onDismissRequest = { showDropdown = false }
                        ) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat) },
                                    onClick = {
                                        category = cat
                                        showDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Product Image Links (Minimum 3 Required)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = imageUrl1,
                        onValueChange = { imageUrl1 = it },
                        label = { Text("Primary Image URL") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("upload_image1")
                    )
                    OutlinedTextField(
                        value = imageUrl2,
                        onValueChange = { imageUrl2 = it },
                        label = { Text("Second Image URL") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("upload_image2")
                    )
                    OutlinedTextField(
                        value = imageUrl3,
                        onValueChange = { imageUrl3 = it },
                        label = { Text("Third Image URL") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("upload_image3")
                    )
                }
            }

            item {
                Text(
                    text = "Uploader Details",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                )
            }

            item {
                OutlinedTextField(
                    value = uploaderName,
                    onValueChange = { uploaderName = it },
                    label = { Text("Your Full Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("upload_uploader_name")
                )
            }

            item {
                OutlinedTextField(
                    value = uploaderContact,
                    onValueChange = { uploaderContact = it },
                    label = { Text("Contact Info (Phone or Email)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("upload_uploader_contact")
                )
            }

            item {
                if (uploadState is UploadUiState.Error) {
                    Text(
                        text = (uploadState as UploadUiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }

            item {
                Button(
                    onClick = {
                        productViewModel.uploadProduct(
                            title = title,
                            description = description,
                            priceStr = priceStr,
                            category = category,
                            images = listOf(imageUrl1, imageUrl2, imageUrl3),
                            uploaderName = uploaderName,
                            uploaderContact = uploaderContact
                        )
                    },
                    enabled = uploadState !is UploadUiState.Loading,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("submit_upload_button")
                ) {
                    if (uploadState is UploadUiState.Loading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = "Publish Listing",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FavoritesScreen(
    productViewModel: ProductViewModel,
    onProductClick: (String) -> Unit
) {
    val favoritesList by productViewModel.favorites.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFBFE))
    ) {
        if (favoritesList.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = "Empty Favorites",
                    tint = Color(0xFF6750A4).copy(alpha = 0.4f),
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No favorites added yet",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1C1B1F)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tap the heart icon on any product details screen to add it here.",
                    fontSize = 13.sp,
                    color = Color(0xFF49454F),
                    modifier = Modifier.padding(horizontal = 32.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp)
            ) {
                // Chunk the favorite products into pairs of 2 to display them in a clean 2-column grid format
                val chunkedList = favoritesList.chunked(2)
                items(chunkedList) { pair ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        pair.forEach { product ->
                            Box(modifier = Modifier.weight(1f)) {
                                GridProductCard(product, onProductClick)
                            }
                        }
                        if (pair.size < 2) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

