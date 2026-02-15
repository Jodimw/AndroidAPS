package app.aaps.ui.search

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * ViewModel for the global search feature.
 * Handles search state, debounced queries, and results.
 */
@Stable
class SearchViewModel @Inject constructor(
    private val searchIndexBuilder: SearchIndexBuilder
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState

    private val _searchQuery = MutableStateFlow("")

    init {
        observeSearchQuery()
    }

    @OptIn(FlowPreview::class)
    private fun observeSearchQuery() {
        _searchQuery
            .debounce(300) // Wait 300ms after typing stops before searching
            .onEach { query ->
                if (query.isBlank()) {
                    _uiState.update { it.copy(results = emptyList(), isSearching = false) }
                } else {
                    _uiState.update { it.copy(isSearching = true) }
                    val results = searchIndexBuilder.search(query)
                    _uiState.update { it.copy(results = results, isSearching = false) }
                }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Called when search mode is activated (search icon tapped).
     */
    fun onSearchModeActivated() {
        _uiState.update { it.copy(isSearchActive = true, query = "", results = emptyList()) }
        _searchQuery.value = ""
    }

    /**
     * Called when search mode is deactivated (back pressed or close tapped).
     */
    fun onSearchModeDeactivated() {
        _uiState.update { it.copy(isSearchActive = false, query = "", results = emptyList()) }
        _searchQuery.value = ""
    }

    /**
     * Called when user types in the search field.
     */
    fun onQueryChanged(query: String) {
        _uiState.update { it.copy(query = query) }
        _searchQuery.value = query
    }

    /**
     * Clears the current search query but keeps search mode active.
     */
    fun clearQuery() {
        _uiState.update { it.copy(query = "", results = emptyList()) }
        _searchQuery.value = ""
    }

    /**
     * Called when a search result is selected.
     * Returns the selected entry for navigation handling.
     */
    fun onResultSelected(entry: SearchIndexEntry): SearchIndexEntry {
        // Close search after selection
        onSearchModeDeactivated()
        return entry
    }

    /**
     * Invalidates the search index, forcing rebuild on next search.
     */
    fun invalidateIndex() {
        searchIndexBuilder.invalidateIndex()
    }
}

/**
 * UI state for the search feature.
 */
data class SearchUiState(
    val isSearchActive: Boolean = false,
    val query: String = "",
    val results: List<SearchIndexEntry> = emptyList(),
    val isSearching: Boolean = false
)
