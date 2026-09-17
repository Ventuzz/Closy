package com.closy.ui.home

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.closy.R
import com.closy.data.model.GarmentItem
import com.closy.data.db.ClosetItemEntity
import com.closy.data.model.Outfit
import com.closy.ui.components.SegmentedTabControl
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
                        onFavoriteToggle = { viewModel.toggleFavorite(selectedOutfit.id) },
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
    val feedListState = rememberLazyListState()
    val isFeedScrolled by remember { derivedStateOf { feedListState.firstVisibleItemIndex > 0 || feedListState.firstVisibleItemScrollOffset > 48 } }
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

        // Segmented Tab Switcher: "Para Ti" vs "Guardados"
        SegmentedTabControl(
            options = listOf("Para Ti", "Guardados"),
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
                    modifier = Modifier.fillMaxSize().animateContentSize(tween(350))
                ) {
                    items(outfitsForTab, key = { it.id }) { outfit ->
                        OutfitCard(
                            outfit = outfit,
                            onCardClick = { viewModel.selectOutfitForDetail(outfit) },
                            onFavoriteToggle = { viewModel.toggleFavorite(outfit.id) }
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
                            imageVector = if (outfit.isSaved) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = if (outfit.isSaved) "Quitar de guardados" else "Guardar",
                            tint = if (outfit.isSaved) Color(0xFFE60023) else Color(0xFF222222),
                            modifier = Modifier.size(20.dp)
                        )
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
                    imageVector = if (outfit.isSaved) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (outfit.isSaved) "Guardado" else "Guardar",
                    tint = if (outfit.isSaved) Color(0xFFE60023) else MaterialTheme.colorScheme.onSurface
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
            text = if (isSavedTab) "No tienes outfits guardados" else "Sin resultados de outfits",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isSavedTab) {
                "Toca el icono de corazón en cualquier outfit para guardarlo aquí."
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

@Composable
private fun ClosetTab(uiState: HomeUiState, viewModel: HomeViewModel) {
    var editing by remember { mutableStateOf<ClosetItemEntity?>(null) }
    var showEditor by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("Todos") }
    val filters = listOf("Todos", "Tops", "Pantalones", "Abrigos", "Calzado", "Accesorios")
    val visibleItems = uiState.closetItems.filter { item ->
        selectedFilter == "Todos" || item.category.equals(selectedFilter, true) ||
            (selectedFilter == "Tops" && item.category == "Parte superior") ||
            (selectedFilter == "Pantalones" && item.category == "Parte inferior") ||
            (selectedFilter == "Accesorios" && item.category == "Accesorio")
    }
    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().padding(start = 28.dp, end = 20.dp, top = 18.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("${uiState.closetItems.size} PRENDAS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 1.5.sp)
                Text("Mi closet", style = MaterialTheme.typography.headlineMedium.copy(fontFamily = FontFamily.Serif), fontWeight = FontWeight.Normal)
            }
            Surface(onClick = { editing = null; showEditor = true }, shape = CircleShape, color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(44.dp)) {
                Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Add, "Agregar prenda", tint = MaterialTheme.colorScheme.onPrimary) }
            }
        }
        LazyRow(contentPadding = PaddingValues(horizontal = 28.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 8.dp)) {
            items(filters) { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter) },
                    shape = PillShape,
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primary, selectedLabelColor = MaterialTheme.colorScheme.onPrimary)
                )
            }
        }
        if (visibleItems.isEmpty()) {
            Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Icon(Icons.Default.Checkroom, null, Modifier.size(72.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(16.dp))
                Text(if (uiState.closetItems.isEmpty()) "Tu closet está listo para crecer" else "No hay prendas en este filtro", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(if (uiState.closetItems.isEmpty()) "Agrega tu primera prenda para recibir recomendaciones personalizadas." else "Selecciona otro filtro o agrega una prenda.", textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(16.dp))
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(start = 28.dp, end = 28.dp, top = 4.dp, bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                gridItems(visibleItems, key = { it.id }) { item ->
                    Card(
                        Modifier.fillMaxWidth().aspectRatio(0.72f).clickable { editing = item; showEditor = true },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant)) {
                            if (item.imageUri.isNotBlank()) {
                                AsyncImage(item.imageUri, item.name, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                            } else {
                                Icon(Icons.Default.Checkroom, null, Modifier.size(64.dp).align(Alignment.Center), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .35f))
                            }
                            Surface(shape = CircleShape, color = Color.White.copy(alpha = .9f), modifier = Modifier.padding(8.dp).size(28.dp).align(Alignment.TopStart)) {
                                Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Edit, "Editar", Modifier.size(15.dp), tint = Color.DarkGray) }
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
            item = editing,
            onDismiss = { showEditor = false },
            onSave = { name, category, color, season, notes, imageUri, size ->
                viewModel.saveClosetItem(editing, name, category, color, season, notes, imageUri, size)
                showEditor = false
            },
            onDelete = editing?.let { item -> { viewModel.deleteClosetItem(item); showEditor = false } }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClosetItemDialog(
    item: ClosetItemEntity?,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String, String, String) -> Unit,
    onDelete: (() -> Unit)?
) {
    val context = LocalContext.current
    var name by remember(item) { mutableStateOf(item?.name.orEmpty()) }
    var category by remember(item) { mutableStateOf(item?.category ?: "Tops") }
    var color by remember(item) { mutableStateOf(item?.color.orEmpty()) }
    var notes by remember(item) { mutableStateOf(item?.notes.orEmpty()) }
    var imageUri by remember(item) { mutableStateOf(item?.imageUri.orEmpty()) }
    var size by remember(item) { mutableStateOf(item?.size.orEmpty()) }
    val photoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            runCatching { context.contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION) }
            imageUri = it.toString()
        }
    }
    val colors = listOf(
        "#F7F4EE", "#111111", "#A7A7A7", "#DCCFB5", "#C9A477", "#805133", "#C93329", "#E9ABC5",
        "#EF7D19", "#F9BE0B", "#20AE67", "#637945", "#2886B8", "#486FA5", "#8D3EB0", "#97001F"
    )
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        LazyColumn(Modifier.fillMaxWidth(), contentPadding = PaddingValues(horizontal = 26.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(64.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surfaceVariant).clickable { photoLauncher.launch(arrayOf("image/*")) }, contentAlignment = Alignment.Center) {
                        if (imageUri.isNotBlank()) AsyncImage(imageUri, "Foto de la prenda", Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                        else Icon(Icons.Default.AddAPhoto, "Agregar foto")
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(if (item == null) "NUEVA PRENDA" else "EDITANDO PRENDA", style = MaterialTheme.typography.labelSmall, letterSpacing = 1.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        OutlinedTextField(name, { name = it }, placeholder = { Text("Nombre de la prenda") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
            item {
                Text("CATEGORÍA", style = MaterialTheme.typography.labelSmall, letterSpacing = 1.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Tops", "Pantalones", "Abrigos", "Calzado", "Accesorios").forEach { value ->
                        FilterChip(selected = category == value, onClick = { category = value }, label = { Text(value) })
                    }
                }
            }
            item {
                Text("TALLA", style = MaterialTheme.typography.labelSmall, letterSpacing = 1.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("XS", "S", "M", "L", "XL", "XXL").forEach { value ->
                        FilterChip(selected = size == value, onClick = { size = if (size == value) "" else value }, label = { Text(value) })
                    }
                }
            }
            item {
                Text("COLOR (OPCIONAL)", style = MaterialTheme.typography.labelSmall, letterSpacing = 1.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(vertical = 8.dp)) {
                    colors.forEach { hex ->
                        Box(
                            Modifier.size(34.dp).background(hexColor(hex), CircleShape).border(if (color.equals(hex, true)) 3.dp else 1.dp, if (color.equals(hex, true)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline, CircleShape).clickable { color = if (color.equals(hex, true)) "" else hex }
                        )
                    }
                }
                OutlinedTextField(value = color, onValueChange = { color = it.take(7) }, label = { Text("Hexadecimal, por ejemplo #111111") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            }
            item { OutlinedTextField(notes, { notes = it }, label = { Text("Notas (opcional)") }, modifier = Modifier.fillMaxWidth()) }
            item {
                Button(onClick = { onSave(name, category, color, item?.season ?: "Todo el año", notes, imageUri, size) }, enabled = name.isNotBlank(), modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(12.dp)) {
                    Text(if (item == null) "Guardar prenda" else "Guardar cambios")
                }
            }
            onDelete?.let { delete ->
                item { OutlinedButton(onClick = delete, Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(12.dp)) { Text("Eliminar prenda", color = MaterialTheme.colorScheme.error) } }
            }
            item { Spacer(Modifier.height(18.dp)) }
        }
    }
}

private fun hexColor(value: String): Color = runCatching {
    Color(android.graphics.Color.parseColor(value))
}.getOrDefault(Color.Transparent)

@Composable
private fun RecommendationsTab(uiState: HomeUiState, viewModel: HomeViewModel) {
    var occasion by remember { mutableStateOf("Diario") }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Text("Recomendaciones", style = MaterialTheme.typography.headlineMedium.copy(fontFamily = FontFamily.Serif), fontWeight = FontWeight.Bold)
            Text("Combinaciones basadas en tu estilo y las prendas de tu closet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        item {
            Text("¿Para qué ocasión?", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Diario", "Trabajo", "Evento", "Fin de semana").forEach { value ->
                    FilterChip(selected = occasion == value, onClick = { occasion = value }, label = { Text(value) })
                }
            }
        }
        item {
            Button(onClick = { viewModel.generateRecommendation(occasion) }, Modifier.fillMaxWidth().height(54.dp), shape = PillShape) {
                Icon(Icons.Default.AutoAwesome, null)
                Spacer(Modifier.width(8.dp))
                Text("Generar recomendación")
            }
        }
        uiState.recommendation?.let { outfit ->
            item {
                Text("Tu combinación sugerida", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                OutfitCard(outfit = outfit, onCardClick = { viewModel.selectOutfitForDetail(outfit) }, onFavoriteToggle = { viewModel.toggleFavorite(outfit.id) })
                Spacer(Modifier.height(10.dp))
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Text(uiState.recommendationReason, Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        }
    }
}

@Composable
private fun ProfileTab(uiState: HomeUiState, viewModel: HomeViewModel, onDarkModeChange: (Boolean) -> Unit, onNavigateToPersonalization: () -> Unit, onLogout: () -> Unit) {
    var notificationsEnabled by remember { mutableStateOf(false) }
    var darkModeEnabled by remember { mutableStateOf(false) }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 28.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(painterResource(R.drawable.ic_closy_logo), "Closy", Modifier.size(28.dp))
                    Text("Closy", style = MaterialTheme.typography.labelSmall, fontFamily = FontFamily.Serif)
                }
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.size(54.dp)) {
                    Box(contentAlignment = Alignment.Center) { Text(uiState.userName.take(1).uppercase(), style = MaterialTheme.typography.titleLarge, fontFamily = FontFamily.Serif) }
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(uiState.userName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                    Text(uiState.userEmail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ProfileStat("${uiState.closetItems.size}", "Prendas", Modifier.weight(1f))
                ProfileStat("${uiState.savedOutfitCount}", "Outfits", Modifier.weight(1f))
            }
        }
        item {
            SectionLabel("PREFERENCIA DE BÚSQUEDA")
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(Modifier.padding(14.dp)) {
                    Text("Buscar outfits para", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Hombre", "Mujer", "Sin género").forEach { option ->
                            Surface(
                                onClick = { viewModel.onGenderPreferenceSelected(option) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                color = if (uiState.activeGenderPreference == option) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                border = BorderStroke(1.dp, if (uiState.activeGenderPreference == option) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
                            ) {
                                Text(option, Modifier.padding(vertical = 10.dp), textAlign = TextAlign.Center, style = MaterialTheme.typography.labelSmall, color = if (uiState.activeGenderPreference == option) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
        item {
            SectionLabel("RECORDATORIOS")
            SettingsToggleCard(Icons.Default.Notifications, "Notificaciones push", "Recordatorio para planear tu outfit", notificationsEnabled) { notificationsEnabled = it }
        }
        item {
            SectionLabel("AJUSTES")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SettingsRow(Icons.Default.Lock, "Privacidad")
                SettingsRow(Icons.Default.Info, "Sobre Closy")
                SettingsToggleCard(Icons.Default.DarkMode, "Modo oscuro", null, darkModeEnabled) {
                    darkModeEnabled = it
                    onDarkModeChange(it)
                }
                OutlinedButton(onClick = onLogout, Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = .25f))) {
                    Icon(Icons.AutoMirrored.Filled.Logout, null, tint = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.width(8.dp))
                    Text("Cerrar sesión", color = MaterialTheme.colorScheme.error)
                }
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
