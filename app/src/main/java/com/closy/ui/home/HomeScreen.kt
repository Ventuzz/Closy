package com.closy.ui.home

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DryCleaning
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.activity.compose.BackHandler
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.closy.R
import com.closy.data.model.GarmentItem
import com.closy.data.db.ClosetGarmentEntity
import com.closy.data.db.ClosetItemEntity
import com.closy.data.model.Outfit
import com.closy.data.repository.ThemeRepository
import com.closy.ui.components.SegmentedTabControl
import com.closy.ui.personalization.GenderOption
import com.closy.ui.theme.ClosyTheme
import com.closy.ui.theme.PillShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToPersonalization: () -> Unit,
    onLogout: () -> Unit,
    onDarkModeChange: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showLogoutBackDialog by remember { mutableStateOf(false) }

    BackHandler(enabled = true) {
        showLogoutBackDialog = true
    }

    LaunchedEffect(Unit) {
        viewModel.resetBottomTab()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            ClosyBottomNavigationBar(
                selectedTab = uiState.selectedBottomTab,
                onTabSelected = viewModel::onBottomTabSelected
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
        ) {
            when (uiState.selectedBottomTab) {
                0 -> MainHomeContent(
                    uiState = uiState,
                    viewModel = viewModel,
                    onNavigateToPersonalization = onNavigateToPersonalization,
                    onLogout = onLogout
                )
                1 -> ClosetTab(uiState = uiState, viewModel = viewModel)
                2 -> RecommendationsTab(uiState = uiState, viewModel = viewModel)
                3 -> ProfileTab(
                    uiState = uiState,
                    viewModel = viewModel,
                    onDarkModeChange = onDarkModeChange,
                    onNavigateToPersonalization = onNavigateToPersonalization,
                    onLogout = onLogout
                )
            }

            // BackHandler Logout Confirmation Dialog
            if (showLogoutBackDialog) {
                AlertDialog(
                    onDismissRequest = { showLogoutBackDialog = false },
                    title = {
                        Text(
                            text = "¿Cerrar sesión?",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    text = {
                        Text(
                            text = "¿Estás seguro/a de que deseas cerrar sesión y salir de Closy?",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showLogoutBackDialog = false
                                onLogout()
                            }
                        ) {
                            Text("Cerrar sesión")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showLogoutBackDialog = false }
                        ) {
                            Text("Cancelar")
                        }
                    }
                )
            }

            // Deletion Confirmation Dialog
            uiState.deleteConfirmationState?.let { deleteState ->
                AlertDialog(
                    onDismissRequest = viewModel::cancelDeletion,
                    title = {
                        Text(
                            text = deleteState.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    text = {
                        Text(
                            text = deleteState.body,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = viewModel::confirmDeletion,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            )
                        ) {
                            Text(deleteState.confirmButtonText)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = viewModel::cancelDeletion) {
                            Text("Cancelar")
                        }
                    }
                )
            }

            // Outfit Detail Bottom Sheet
            uiState.selectedOutfitForDetail?.let { selectedOutfit ->
                ModalBottomSheet(
                    onDismissRequest = { viewModel.selectOutfitForDetail(null) },
                    sheetState = sheetState,
                    containerColor = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                ) {
                    OutfitDetailBottomSheetContent(
                        outfit = selectedOutfit,
                        onFavoriteToggle = {
                            if (selectedOutfit.isFavorite || selectedOutfit.isSaved) {
                                viewModel.requestUnsaveOutfit(selectedOutfit)
                            } else {
                                viewModel.toggleFavorite(selectedOutfit.id)
                            }
                        },
                        onClose = { viewModel.selectOutfitForDetail(null) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MainHomeContent(
    uiState: HomeUiState,
    viewModel: HomeViewModel,
    onNavigateToPersonalization: () -> Unit,
    onLogout: () -> Unit
) {
    var isUserMenuExpanded by remember { mutableStateOf(false) }
    var isFeedScrolled by remember { mutableStateOf(false) }
    val logoSize by animateDpAsState(if (isFeedScrolled) 24.dp else 28.dp, label = "HomeLogoSize")

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top Bar Header: Logo + Active Style Preference Selector + Options Menu
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_closy_logo),
                    contentDescription = "Closy Logo",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(logoSize)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Closy",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box {
                    IconButton(onClick = { isUserMenuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Opciones",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    DropdownMenu(
                        expanded = isUserMenuExpanded,
                        onDismissRequest = { isUserMenuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Ajustar preferencias") },
                            onClick = {
                                isUserMenuExpanded = false
                                onNavigateToPersonalization()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Cerrar sesión") },
                            onClick = {
                                isUserMenuExpanded = false
                                onLogout()
                            }
                        )
                    }
                }
            }
        }

        // Header Title: "Ideas de outfits" (Serif typography, italic for "outfits")
        AnimatedVisibility(
            visible = !isFeedScrolled,
            enter = expandVertically(animationSpec = tween(350)) + fadeIn(tween(300)),
            exit = shrinkVertically(animationSpec = tween(350)) + fadeOut(tween(220))
        ) {
            Text(
                text = buildAnnotatedString {
                    append("Ideas de ")
                    withStyle(style = SpanStyle(fontFamily = FontFamily.Serif, fontStyle = FontStyle.Italic)) {
                        append("outfits")
                    }
                },
                style = MaterialTheme.typography.headlineLarge.copy(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
        }

        // Segmented Tab Switcher: "Para Ti" vs "Favoritos"
        SegmentedTabControl(
            options = listOf("Para Ti", "Favoritos"),
            selectedIndex = uiState.selectedSegmentTab,
            onOptionSelected = viewModel::onSegmentTabSelected,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Category / Filter Chips Bar
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(uiState.categories) { category ->
                val isSelected = category.equals(uiState.selectedCategory, ignoreCase = true)
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.onCategorySelected(category) },
                    label = {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    shape = PillShape,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Feed list of outfits with AnimatedContent transition
        AnimatedContent(
            targetState = uiState.selectedSegmentTab,
            transitionSpec = {
                if (targetState > initialState) {
                    (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                        slideOutHorizontally { width -> -width } + fadeOut()
                    )
                } else {
                    (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                        slideOutHorizontally { width -> -width } + fadeOut()
                    )
                }
            },
            label = "TabTransition",
            modifier = Modifier.weight(1f)
        ) { segmentTab ->
            val feedListState = key(segmentTab, uiState.activeGenderPreference) {
                rememberLazyListState()
            }
            val isCurrentTabScrolled by remember {
                derivedStateOf { feedListState.firstVisibleItemIndex > 0 || feedListState.firstVisibleItemScrollOffset > 48 }
            }
            if (segmentTab == uiState.selectedSegmentTab) {
                SideEffect {
                    isFeedScrolled = isCurrentTabScrolled
                }
            }

            val isSavedTab = segmentTab == 1
            val outfitsForTab = if (isSavedTab) {
                uiState.outfits.filter { it.isSaved }
            } else {
                uiState.outfits
            }

            if (outfitsForTab.isEmpty()) {
                EmptyStateView(
                    category = uiState.selectedCategory,
                    isSavedTab = isSavedTab,
                    onReset = {
                        viewModel.onCategorySelected("Todos")
                        if (isSavedTab) {
                            viewModel.onSegmentTabSelected(0)
                        }
                    }
                )
            } else {
                LazyColumn(
                    state = feedListState,
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(outfitsForTab, key = { it.id }) { outfit ->
                        OutfitCard(
                            outfit = outfit,
                            onCardClick = { viewModel.selectOutfitForDetail(outfit) },
                            onFavoriteToggle = {
                                if (outfit.isFavorite || outfit.isSaved) {
                                    viewModel.requestUnsaveOutfit(outfit)
                                } else {
                                    viewModel.toggleFavorite(outfit.id)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OutfitCard(
    outfit: Outfit,
    onCardClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
    ) {
        Column {
            // Image Visual Container with overlay elements
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(outfit.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = outfit.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Pinterest Badge Overlay (Top Left)
                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.95f),
                    shadowElevation = 3.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_pinterest),
                            contentDescription = "Pinterest",
                            tint = Color(0xFFE60023),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "via Pinterest · ${outfit.pinterestHandle}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF111111)
                        )
                    }
                }

                // Bookmark / Favorite Circular Button (Top Right)
                Surface(
                    onClick = onFavoriteToggle,
                    modifier = Modifier
                        .padding(12.dp)
                        .size(38.dp)
                        .align(Alignment.TopEnd),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.95f),
                    shadowElevation = 3.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (outfit.isSaved || outfit.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = if (outfit.isSaved || outfit.isFavorite) "Quitar de favoritos" else "Añadir a favoritos",
                            tint = if (outfit.isSaved || outfit.isFavorite) Color(0xFFE60023) else Color(0xFF222222),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Closet Garment Match Badge Overlay (Bottom Left)
                if (outfit.matchedGarmentsCount > 0) {
                    Surface(
                        modifier = Modifier
                            .padding(12.dp)
                            .align(Alignment.BottomStart),
                        shape = PillShape,
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.95f),
                        shadowElevation = 3.dp,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "✨ ${outfit.matchedGarmentsCount} ${if (outfit.matchedGarmentsCount == 1) "prenda de tu closet coincide" else "prendas de tu closet coinciden"}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            // Card Content Area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Title on left (Serif typography) + Style category on right
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = outfit.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = outfit.styleCategory.uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Garment summary row: 2-3 thumbnail previews on left + summary text on right
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        val thumbnails = outfit.garmentThumbnails.ifEmpty {
                            listOf(outfit.imageUrl)
                        }.take(3)

                        thumbnails.forEach { thumbUrl ->
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(thumbUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            )
                        }
                    }

                    Text(
                        text = outfit.garmentSummary.ifEmpty {
                            outfit.garments.joinToString(" · ") { it.name }
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Hashtag chips row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val tagsToDisplay = if (outfit.hashtags.isNotEmpty()) outfit.hashtags else outfit.tags.map { "#${it.lowercase()}" }
                    tagsToDisplay.forEach { tag ->
                        val formattedTag = if (tag.startsWith("#")) tag else "#$tag"
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        ) {
                            Text(
                                text = formattedTag,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ClosyBottomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val items = listOf(
                Triple(0, "Inicio", Icons.Default.Home),
                Triple(1, "Closet", Icons.Default.Checkroom),
                Triple(2, "Generar", Icons.Default.AutoAwesome),
                Triple(3, "Perfil", Icons.Default.Person)
            )

            items.forEach { (index, title, icon) ->
                val isSelected = selectedTab == index
                val contentColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)

                Column(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onTabSelected(index) }
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        modifier = Modifier.size(20.dp),
                        tint = contentColor
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = contentColor,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OutfitDetailBottomSheetContent(
    outfit: Outfit,
    onFavoriteToggle: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        // Image Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(outfit.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = outfit.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Close button top right
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .padding(12.dp)
                    .align(Alignment.TopEnd)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cerrar",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            // Pinterest Badge
            Surface(
                modifier = Modifier
                    .padding(12.dp)
                    .align(Alignment.BottomStart),
                shape = PillShape,
                color = Color.White,
                shadowElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_pinterest),
                        contentDescription = "Pinterest",
                        tint = Color(0xFFE60023),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "via Pinterest · ${outfit.pinterestHandle}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111111)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Title and Category Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = outfit.title,
                    style = MaterialTheme.typography.headlineSmall.copy(fontFamily = FontFamily.Serif),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = PillShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = outfit.styleCategory,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        shape = PillShape,
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = outfit.genderPreference,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Favorite button
            IconButton(
                onClick = onFavoriteToggle,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                    .size(44.dp)
            ) {
                Icon(
                    imageVector = if (outfit.isSaved || outfit.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (outfit.isSaved || outfit.isFavorite) "Quitar de favoritos" else "Añadir a favoritos",
                    tint = if (outfit.isSaved || outfit.isFavorite) Color(0xFFE60023) else MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Prenda Breakdown Title
        Text(
            text = "Composición del Outfit (Prendas)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Garments List
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            outfit.garments.forEach { garment ->
                GarmentItemRow(garment = garment)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Tags FlowRow
        val tagsToDisplay = if (outfit.hashtags.isNotEmpty()) outfit.hashtags else outfit.tags.map { "#${it.lowercase()}" }
        if (tagsToDisplay.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                tagsToDisplay.forEach { tag ->
                    val formattedTag = if (tag.startsWith("#")) tag else "#$tag"
                    Text(
                        text = formattedTag,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                PillShape
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Action Buttons: "Ver en Pinterest"
        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, outfit.pinterestUrl.toUri())
                context.startActivity(intent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = PillShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE60023),
                contentColor = Color.White
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Ver en Pinterest",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun GarmentItemRow(
    garment: GarmentItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = when {
                            garment.category.contains("Calzado", ignoreCase = true) -> Icons.Default.ShoppingBag
                            garment.category.contains("Accesorios", ignoreCase = true) -> Icons.Default.FilterList
                            else -> Icons.Default.DryCleaning
                        },
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = garment.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = garment.category + if (!garment.brandOrNote.isNullOrEmpty()) " • ${garment.brandOrNote}" else "",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun EmptyStateView(
    category: String,
    isSavedTab: Boolean,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (isSavedTab) Icons.Default.BookmarkBorder else Icons.Default.Search,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isSavedTab) "No tienes outfits favoritos" else "Sin resultados de outfits",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isSavedTab) {
                "Toca el ícono de corazón en cualquier outfit para agregarlo a tus favoritos."
            } else {
                "No encontramos outfits en la categoría \"$category\". Prueba cambiando los filtros."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            onClick = onReset,
            shape = PillShape
        ) {
            Text(if (isSavedTab) "Explorar outfits" else "Restablecer filtros")
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ClosetTab(uiState: HomeUiState, viewModel: HomeViewModel) {
    var editingGarment by remember { mutableStateOf<ClosetGarmentEntity?>(null) }
    var editingItem by remember { mutableStateOf<ClosetItemEntity?>(null) }
    var showEditor by remember { mutableStateOf(false) }
    val filters = listOf("Todos", "Camisas", "Tops", "Sacos", "Pantalones", "Calzado", "Accesorios")

    val garmentsList = uiState.closetGarments
    val itemsList = uiState.closetItems

    val totalCount = uiState.closetGarmentsCount.coerceAtLeast(itemsList.size)
    val visibleGarments = uiState.filteredClosetGarments
    val selectedFilter = uiState.selectedClosetCategory

    val visibleItems = itemsList.filter { item ->
        selectedFilter == "Todos" || item.category.equals(selectedFilter, true)
    }

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 28.dp, end = 20.dp, top = 18.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("$totalCount PRENDAS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 1.5.sp)
                Text("Mi closet", style = MaterialTheme.typography.headlineMedium.copy(fontFamily = FontFamily.Serif), fontWeight = FontWeight.Normal)
            }
            Surface(onClick = { editingGarment = null; editingItem = null; showEditor = true }, shape = CircleShape, color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(44.dp)) {
                Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Add, "Agregar prenda", tint = MaterialTheme.colorScheme.onPrimary) }
            }
        }
        LazyRow(contentPadding = PaddingValues(horizontal = 28.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 8.dp)) {
            items(filters) { filter ->
                FilterChip(
                    selected = selectedFilter.equals(filter, ignoreCase = true),
                    onClick = { viewModel.onClosetCategorySelected(filter) },
                    label = { Text(filter) },
                    shape = PillShape,
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primary, selectedLabelColor = MaterialTheme.colorScheme.onPrimary)
                )
            }
        }

        if (totalCount == 0) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.Checkroom, null, Modifier.size(72.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(16.dp))
                Text("Tu closet está vacío", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Tu closet está vacío. Agrega prendas para empezar a crear combinaciones.",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(20.dp))
                Button(onClick = { editingGarment = null; editingItem = null; showEditor = true }, shape = PillShape) {
                    Icon(Icons.Default.Add, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Agregar prenda")
                }
            }
        } else if (garmentsList.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(start = 28.dp, end = 28.dp, top = 4.dp, bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                gridItems(visibleGarments, key = { garment: ClosetGarmentEntity -> garment.id }) { garment: ClosetGarmentEntity ->
                    Card(
                        Modifier.fillMaxWidth().aspectRatio(0.72f).clickable { editingGarment = garment; editingItem = null; showEditor = true },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant)) {
                            if (garment.imageUrl.isNotBlank()) {
                                AsyncImage(garment.imageUrl, garment.name, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                            } else {
                                Icon(Icons.Default.Checkroom, null, Modifier.size(64.dp).align(Alignment.Center), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .35f))
                            }

                            if (garment.color.isNotBlank()) {
                                Box(
                                    Modifier
                                        .padding(9.dp)
                                        .size(16.dp)
                                        .align(Alignment.TopEnd)
                                        .background(hexColor(garment.color), CircleShape)
                                        .border(1.dp, Color.White, CircleShape)
                                )
                            }

                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomStart)
                                    .background(Color.Black.copy(alpha = .55f))
                                    .padding(10.dp)
                            ) {
                                Text(
                                    garment.name,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    listOf(garment.category, garment.styleTag).filter { it.isNotBlank() }.joinToString(" · "),
                                    color = Color.White.copy(alpha = .8f),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(start = 28.dp, end = 28.dp, top = 4.dp, bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                gridItems(visibleItems, key = { item: ClosetItemEntity -> item.id }) { item: ClosetItemEntity ->
                    Card(
                        Modifier.fillMaxWidth().aspectRatio(0.72f).clickable { editingGarment = null; editingItem = item; showEditor = true },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant)) {
                            if (item.imageUri.isNotBlank()) {
                                AsyncImage(item.imageUri, item.name, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                            } else {
                                Icon(Icons.Default.Checkroom, null, Modifier.size(64.dp).align(Alignment.Center), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .35f))
                            }
                            if (item.color.isNotBlank()) {
                                Box(Modifier.padding(9.dp).size(14.dp).align(Alignment.TopEnd).background(hexColor(item.color), CircleShape).border(1.dp, Color.White, CircleShape))
                            }
                            Column(Modifier.fillMaxWidth().align(Alignment.BottomStart).background(Color.Black.copy(alpha = .42f)).padding(10.dp)) {
                                Text(item.name, color = Color.White, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(listOf(item.category, item.size).filter { it.isNotBlank() }.joinToString(" · "), color = Color.White.copy(alpha = .8f), style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }
    if (showEditor) {
        ClosetItemDialog(
            garment = editingGarment,
            item = editingItem,
            onDismiss = {
                showEditor = false
                editingGarment = null
                editingItem = null
            },
            onSaveGarment = { name, category, color, imageUrl, styleTag ->
                val currentGarment = editingGarment
                val currentItem = editingItem
                if (currentGarment != null) {
                    viewModel.updateGarment(currentGarment.id, name, category, color, imageUrl, styleTag)
                } else if (currentItem != null) {
                    viewModel.saveClosetItem(currentItem, name, category, color, styleTag, "", imageUrl, "")
                } else {
                    viewModel.addGarment(name, category, color, imageUrl, styleTag)
                }
                showEditor = false
                editingGarment = null
                editingItem = null
            },
            onDelete = when {
                editingGarment != null -> {
                    val g = editingGarment!!
                    {
                        viewModel.requestDeleteGarment(g)
                        showEditor = false
                        editingGarment = null
                    }
                }
                editingItem != null -> {
                    val i = editingItem!!
                    {
                        viewModel.requestDeleteClosetItem(i)
                        showEditor = false
                        editingItem = null
                    }
                }
                else -> null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun ClosetItemDialog(
    garment: ClosetGarmentEntity? = null,
    item: ClosetItemEntity? = null,
    onDismiss: () -> Unit,
    onSaveGarment: (name: String, category: String, color: String, imageUrl: String, styleTag: String) -> Unit,
    onDelete: (() -> Unit)?
) {
    val context = LocalContext.current
    var name by remember(garment, item) { mutableStateOf(garment?.name ?: item?.name.orEmpty()) }
    var category by remember(garment, item) { mutableStateOf(garment?.category ?: item?.category ?: "Tops") }
    var color by remember(garment, item) { mutableStateOf(garment?.color ?: item?.color.orEmpty()) }
    var imageUrl by remember(garment, item) { mutableStateOf(garment?.imageUrl ?: item?.imageUri.orEmpty()) }
    var styleTag by remember(garment, item) { mutableStateOf(garment?.styleTag ?: item?.season.orEmpty().ifBlank { "Casual" }) }

    val photoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            runCatching { context.contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION) }
            imageUrl = it.toString()
        }
    }
    val categoriesList = listOf("Camisas", "Tops", "Sacos", "Pantalones", "Calzado", "Accesorios")
    val styleTagsList = listOf("Casual", "Formal", "Urbano", "Básico", "Elegante", "Verano", "Fiesta", "Trabajo")
    val colors = listOf(
        "#F7F4EE", "#111111", "#A7A7A7", "#DCCFB5", "#C9A477", "#805133", "#C93329", "#E9ABC5",
        "#EF7D19", "#F9BE0B", "#20AE67", "#637945", "#2886B8", "#486FA5", "#8D3EB0", "#97001F"
    )

    val isEditing = garment != null || item != null

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        LazyColumn(Modifier.fillMaxWidth(), contentPadding = PaddingValues(horizontal = 26.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { photoLauncher.launch(arrayOf("image/*")) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageUrl.isNotBlank()) {
                            AsyncImage(imageUrl, "Foto de la prenda", Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                        } else {
                            Icon(Icons.Default.AddAPhoto, "Agregar foto")
                        }
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            if (isEditing) "EDITANDO PRENDA" else "NUEVA PRENDA",
                            style = MaterialTheme.typography.labelSmall,
                            letterSpacing = 1.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Nombre de la prenda") },
                            placeholder = { Text("Ej. Camisa Oxford") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            item {
                Text("CATEGORÍA", style = MaterialTheme.typography.labelSmall, letterSpacing = 1.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    categoriesList.forEach { value ->
                        FilterChip(
                            selected = category.equals(value, ignoreCase = true),
                            onClick = { category = value },
                            label = { Text(value) }
                        )
                    }
                }
            }
            item {
                Text("COLOR", style = MaterialTheme.typography.labelSmall, letterSpacing = 1.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    colors.forEach { hex ->
                        Box(
                            Modifier
                                .size(34.dp)
                                .background(hexColor(hex), CircleShape)
                                .border(
                                    if (color.equals(hex, true)) 3.dp else 1.dp,
                                    if (color.equals(hex, true)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                    CircleShape
                                )
                                .clickable { color = if (color.equals(hex, true)) "" else hex }
                        )
                    }
                }
                OutlinedTextField(
                    value = color,
                    onValueChange = { color = it.take(20) },
                    label = { Text("Color (Hexadecimal o nombre)") },
                    placeholder = { Text("#111111 o Negro") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                Text("ESTILO / ETIQUETA", style = MaterialTheme.typography.labelSmall, letterSpacing = 1.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    styleTagsList.forEach { value ->
                        FilterChip(
                            selected = styleTag.equals(value, ignoreCase = true),
                            onClick = { styleTag = value },
                            label = { Text(value) }
                        )
                    }
                }
            }
            item {
                Button(
                    onClick = {
                        onSaveGarment(name, category, color, imageUrl, styleTag)
                    },
                    enabled = name.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (isEditing) "Guardar cambios" else "Agregar")
                }
            }
            onDelete?.let { delete ->
                item {
                    OutlinedButton(
                        onClick = delete,
                        Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Eliminar prenda", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
            item { Spacer(Modifier.height(18.dp)) }
        }
    }
}

private fun hexColor(value: String): Color = runCatching {
    Color(android.graphics.Color.parseColor(value))
}.getOrDefault(Color.Transparent)

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RecommendationsTab(uiState: HomeUiState, viewModel: HomeViewModel) {
    var occasion by remember { mutableStateOf("Diario") }
    val closetGarments = uiState.closetGarments
    val styleTags = uiState.availableStyleTags

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Column {
                Text(
                    "Generar combinación",
                    style = MaterialTheme.typography.headlineMedium.copy(fontFamily = FontFamily.Serif),
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Selecciona los estilos para buscar combinaciones de outfits según tus gustos.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Section 1: Tag / Style Filter Chips
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        "Estilos y Filtros",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        styleTags.forEach { tag ->
                            val isSelected = uiState.selectedStyleTags.contains(tag)
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.toggleStyleTag(tag) },
                                label = { Text(tag) },
                                leadingIcon = if (isSelected) {
                                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                } else null,
                                shape = PillShape
                            )
                        }
                    }
                }
            }
        }

        // Action Button
        item {
            Button(
                onClick = { viewModel.generateOutfitCombination(occasion) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = PillShape,
                enabled = true
            ) {
                Icon(Icons.Default.AutoAwesome, null)
                Spacer(Modifier.width(8.dp))
                Text("Generar Combinación", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }

        // Recommendation / Pinterest Outfit Match Results
        if (uiState.recommendationReason.isNotBlank()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(12.dp))
                        Text(
                            uiState.recommendationReason,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        val generatedOutfits = uiState.generatedCombinationOutfits.ifEmpty {
            listOfNotNull(uiState.recommendation)
        }

        if (generatedOutfits.isNotEmpty()) {
            item {
                Text(
                    "Combinaciones sugeridas",
                    style = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamily.Serif),
                    fontWeight = FontWeight.Bold
                )
            }

            items(generatedOutfits, key = { outfitItem: Outfit -> outfitItem.id }) { outfitItem: Outfit ->
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutfitCard(
                        outfit = outfitItem,
                        onCardClick = { viewModel.selectOutfitForDetail(outfitItem) },
                        onFavoriteToggle = {
                            if (outfitItem.isFavorite || outfitItem.isSaved) {
                                viewModel.requestUnsaveOutfit(outfitItem)
                            } else {
                                viewModel.toggleFavorite(outfitItem.id)
                            }
                        }
                    )

                    // Item Breakdown Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    ) {
                        Column(Modifier.padding(14.dp)) {
                            Text(
                                "Desglose de prendas de esta combinación:",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(Modifier.height(8.dp))
                            outfitItem.garments.forEach { garmentItem: GarmentItem ->
                                val isMatchedInCloset = closetGarments.any { closet: ClosetGarmentEntity ->
                                    closet.name.contains(garmentItem.name, ignoreCase = true) ||
                                    garmentItem.name.contains(closet.name, ignoreCase = true) ||
                                    closet.category.equals(garmentItem.category, ignoreCase = true)
                                }
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                        Icon(
                                            imageVector = if (isMatchedInCloset) Icons.Default.Check else Icons.Default.ShoppingBag,
                                            contentDescription = null,
                                            tint = if (isMatchedInCloset) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            text = garmentItem.name,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Surface(
                                        shape = PillShape,
                                        color = if (isMatchedInCloset) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Text(
                                            text = if (isMatchedInCloset) "✨ De tu closet" else "Inspiración",
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isMatchedInCloset) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(10.dp))

                            val isFav = outfitItem.isFavorite || outfitItem.isSaved
                            Button(
                                onClick = {
                                    if (isFav) {
                                        viewModel.requestUnsaveOutfit(outfitItem)
                                    } else {
                                        viewModel.toggleFavorite(outfitItem.id)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = PillShape,
                                colors = if (isFav) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary) else ButtonDefaults.buttonColors()
                            ) {
                                Icon(
                                    imageVector = if (isFav) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = if (isFav) "En favoritos" else "Guardar"
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(if (isFav) "En favoritos" else "Guardar")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileTab(
    uiState: HomeUiState,
    viewModel: HomeViewModel,
    onDarkModeChange: (Boolean) -> Unit,
    onNavigateToPersonalization: () -> Unit,
    onLogout: () -> Unit
) {
    val isDarkMode by ThemeRepository.isDarkMode.collectAsStateWithLifecycle()

    val styleOptions = remember {
        listOf(
            GenderOption(
                title = "Hombre",
                subtitle = "Ideas y estilos para hombres",
                imageUrl = "https://images.unsplash.com/photo-1617137984095-74e4e5e3613f?w=300",
                iconResId = R.drawable.ic_gender_hombre
            ),
            GenderOption(
                title = "Mujer",
                subtitle = "Ideas y estilos para mujeres",
                imageUrl = "https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?w=300",
                iconResId = R.drawable.ic_gender_mujer
            ),
            GenderOption(
                title = "Sin género",
                subtitle = "Estilos neutros y combinables",
                imageUrl = "https://images.unsplash.com/photo-1529139574466-a303027c1d8b?w=300",
                iconResId = R.drawable.ic_gender_singenero
            )
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 20.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Centered Circular User Avatar (100.dp) & Large Serif Display Name (26.sp) with Email Subtitle
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(100.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = uiState.userName.take(1).uppercase().ifEmpty { "C" },
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontSize = 38.sp,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = uiState.userName,
                    style = TextStyle(
                        fontSize = 26.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = uiState.userEmail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Profile Stats
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProfileStat("${uiState.closetGarmentsCount}", "Prendas en Closet", Modifier.weight(1f))
                ProfileStat("${uiState.savedOutfitsCount}", "Outfits Favoritos", Modifier.weight(1f))
            }
        }

        // Section: Preferencia de Estilo
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                SectionLabel("PREFERENCIA DE ESTILO")
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    styleOptions.forEach { option ->
                        val isSelected = uiState.activeGenderPreference == option.title
                        val borderStroke = if (isSelected) {
                            BorderStroke(2.dp, Color(0xFF111111))
                        } else {
                            BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { viewModel.onGenderPreferenceSelected(option.title) }
                                .border(borderStroke, RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) {
                                    MaterialTheme.colorScheme.surfaceVariant
                                } else {
                                    MaterialTheme.colorScheme.surface
                                }
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.size(54.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        AsyncImage(
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(option.imageUrl)
                                                .crossfade(true)
                                                .placeholder(option.iconResId)
                                                .error(option.iconResId)
                                                .build(),
                                            contentDescription = option.title,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = option.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = option.subtitle,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                if (isSelected) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFF111111),
                                        modifier = Modifier.size(22.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Seleccionado",
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section: Detalles de la cuenta
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                SectionLabel("DETALLES DE LA CUENTA")
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Correo electrónico",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = uiState.userEmail,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Estado de la cuenta",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(Color(0xFF20AE67), CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (uiState.userEmail.contains("invitado")) "Invitado" else "Activa",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section: Ajustes (Modo oscuro)
        item {
            SettingsToggleCard(
                icon = Icons.Default.DarkMode,
                title = "Modo oscuro",
                subtitle = "Cambiar apariencia de la aplicación",
                checked = isDarkMode,
                onCheckedChange = { enabled ->
                    ThemeRepository.setDarkMode(enabled)
                    onDarkModeChange(enabled)
                }
            )
        }

        // Action button: Prominent "Cerrar Sesión" dark pill button (red in dark mode)
        item {
            Spacer(modifier = Modifier.height(8.dp))
            val logoutBg = if (isDarkMode) Color(0xFFD32F2F) else Color(0xFF111111)
            val logoutTextColor = Color.White
            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = PillShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = logoutBg,
                    contentColor = logoutTextColor
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = logoutTextColor
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Cerrar Sesión",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = logoutTextColor
                )
            }
        }
    }
}

@Composable
private fun ProfileStat(value: String, label: String, modifier: Modifier = Modifier) {
    Card(modifier, shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(Modifier.fillMaxWidth().padding(vertical = 18.dp, horizontal = 6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamily.Serif), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 1.4.sp, modifier = Modifier.padding(bottom = 8.dp))
}

@Composable
private fun SettingsRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.width(12.dp))
            Text(title, Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
            Text("›", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SettingsToggleCard(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String?, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, Modifier.size(17.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyMedium)
                subtitle?.let { Text(it, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    ClosyTheme {
        HomeScreen(
            onNavigateToPersonalization = {},
            onLogout = {}
        )
    }
}
