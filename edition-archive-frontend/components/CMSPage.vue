<template>
  <div>
    <client-only>
      <!-- Action Buttons -->
      <div class="btn-block d-flex flex-nowrap flex-row justify-content-end mb-3">
        <div v-if="model.authHeader && !model.edit && !model.pageNotFound && model.currentVersion?.status !== 'archived'" class="btn-group" role="group">
          <button v-if="model.permissions?.write" class="btn btn-primary" type="button" @click="startEdit">
            <i class="bi bi-pencil-square"> </i>
            {{ $t("cms.page.editTranslation") }}
          </button>
          <button v-if="model.permissions?.delete && model.currentVersion?.status === 'published'"
                  class="btn btn-warning"
                  @click="archivePage"
          >
            <i class="bi bi-archive"> </i>
            {{ $t("cms.page.archive") }}
          </button>
        </div>
      </div>
    </client-only>

    <div v-if="model">
      <div v-if="model.loading">
        <div class="spinner-border" role="status">
          <span class="visually-hidden">{{ $t("cms.page.loading") }}</span>
        </div>
      </div>

      <!-- Page Not Found - Option to Create -->
      <client-only>
        <div v-if="model.pageNotFound && model.authHeader">
          <div class="card">
            <div class="card-body text-center">
              <i class="bi bi-file-earmark-plus display-1 text-muted mb-3"> </i>
              <h4>{{ $t('cms.page.notFound.title') }}</h4>
              <p class="text-muted">{{ $t('cms.page.notFound.message', { slug: props.slug }) }}</p>
              <button 
                v-if="model.permissions?.write"
                class="btn btn-primary btn-lg" 
                type="button" 
                :disabled="model.isCreatingPage"
                @click="createNewPage"
              >
                <span v-if="model.isCreatingPage" class="spinner-border spinner-border-sm me-2" role="status"></span>
                <i v-else class="bi bi-plus-circle me-2"> </i>
                {{ $t('cms.page.notFound.createButton') }}
              </button>
              <p v-else class="text-muted mt-3">
                <i class="bi bi-lock"> </i>
                {{ $t('cms.page.noWritePermission') }}
              </p>
            </div>
          </div>
        </div>
      </client-only>

      <!-- Archived Page -->
      <client-only>
        <div v-if="model.currentVersion?.status === 'archived' && model.authHeader && !model.edit">
          <div class="card">
            <div class="card-body text-center">
              <i class="bi bi-archive display-1 text-muted mb-3"> </i>
              <h4>{{ $t('cms.page.archived.title') }}</h4>
              <p class="text-muted">{{ $t('cms.page.archived.message') }}</p>
              <button 
                v-if="model.permissions?.write"
                class="btn btn-primary btn-lg" 
                type="button" 
                @click="restoreAndEdit"
              >
                <i class="bi bi-arrow-counterclockwise me-2"> </i>
                {{ $t('cms.page.archived.restoreButton') }}
              </button>
              <p v-else class="text-muted mt-3">
                <i class="bi bi-lock"> </i>
                {{ $t('cms.page.noWritePermission') }}
              </p>
            </div>
          </div>
        </div>
      </client-only>

      <!-- View Mode with Language Tabs -->
      <div v-if="!model.loading && !model.pageNotFound && !model.edit && model.currentVersion && model.currentVersion.status !== 'archived'">
        <!-- Draft-Warnung -->
        <div v-if="model.currentVersion.status === 'draft'" class="alert alert-warning d-flex align-items-center mb-3" role="alert">
          <i class="bi bi-exclamation-triangle-fill me-2"> </i>
          <div>
            <strong>{{ $t('cms.page.draftWarning.title') }}</strong>
            {{ $t('cms.page.draftWarning.message') }}
          </div>
        </div>

        <ul class="nav nav-tabs" role="tablist">
          <li v-for="lang in orderedLanguages" v-show="hasTranslation(lang)" :key="lang" class="nav-item" role="presentation">
            <button
              :class="['nav-link', { active: model.activeViewTab === lang }]"
              type="button"
              role="tab"
              @click="setActiveViewTab(lang)"
            >
              {{ $t(`language.${lang}`) }}
            </button>
          </li>
        </ul>
        <div class="tab-content p-3">
          <div v-if="activeViewTranslation">
            <DynamicCompiled :template="activeViewTranslation.content"/>
          </div>
          <div v-else class="text-muted">
            {{ $t('cms.page.noContentForLanguage') }}
          </div>
        </div>
      </div>

      <!-- Edit Mode with Language Tabs -->
      <client-only>
        <div v-if="model.edit && model.editTranslations">
          <div class="mb-3">
            <ul class="nav nav-tabs" role="tablist">
              <li v-for="lang in orderedLanguages" :key="lang" class="nav-item" role="presentation">
                <button
                  :class="['nav-link', { active: model.activeEditTab === lang }]"
                  type="button"
                  role="tab"
                  @click="setActiveEditTab(lang)"
                >
                  {{ $t(`language.${lang}`) }}
                  <span v-if="isNewTranslation(lang)" class="badge bg-info ms-1">{{ $t('cms.page.new') }}</span>
                </button>
              </li>
            </ul>
            <div class="tab-content border border-top-0 p-3">
              <div v-for="lang in orderedLanguages" :key="lang" :class="['tab-pane', { active: model.activeEditTab === lang }]">
                <div v-if="model.activeEditTab === lang">
                  <div class="mb-3">
                    <label :for="'title-' + lang" class="form-label">{{ $t('cms.page.title') }}</label>
                    <input
                      :id="'title-' + lang"
                      v-model="model.editTranslations[lang].title"
                      type="text"
                      class="form-control"
                      :placeholder="$t('cms.page.titlePlaceholder')"
                    />
                  </div>
                  <div class="mb-3">
                    <label class="form-label">{{ $t('cms.page.content') }}</label>
                    <CMSPageEditor
                      :content="model.editTranslations[lang].content"
                      @cancel="stopEdit"
                      @fileUpload="fileUpload"
                      @save="(content: string) => updateEditContent(lang, content)"
                    />
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Versionshistorie -->
          <div class="card mb-3">
            <div class="card-header d-flex justify-content-between align-items-center">
              <span>
                <i class="bi bi-clock-history"> </i>
                {{ $t('cms.page.versionHistory') }}
              </span>
              <button 
                class="btn btn-sm btn-outline-secondary" 
                type="button"
                @click="toggleVersionHistory"
              >
                <i :class="model.showVersionHistory ? 'bi bi-chevron-up' : 'bi bi-chevron-down'"> </i>
              </button>
            </div>
            <div v-if="model.showVersionHistory" class="card-body">
              <div v-if="model.loadingVersions" class="text-center">
                <div class="spinner-border spinner-border-sm" role="status">
                  <span class="visually-hidden">{{ $t('cms.page.loading') }}</span>
                </div>
              </div>
              <div v-else-if="model.versions.length === 0" class="text-muted">
                {{ $t('cms.page.noVersions') }}
              </div>
              <ul v-else class="list-group list-group-flush">
                <li 
                  v-for="version in model.versions" 
                  :key="version.version_number"
                  class="list-group-item d-flex justify-content-between align-items-start"
                >
                  <div class="ms-2 me-auto">
                    <div class="fw-bold">
                      {{ $t('cms.page.version') }} {{ version.version_number }}
                      <span 
                        :class="['badge', 'ms-1', getStatusBadgeClass(version.status)]"
                      >
                        {{ $t('cms.page.status.' + version.status) }}
                      </span>
                    </div>
                    <small class="text-muted">
                      {{ formatDate(version.created_at) }} - {{ version.created_by }}
                    </small>
                    <div v-if="version.comment" class="small text-muted fst-italic">
                      {{ version.comment }}
                    </div>
                  </div>
                  <button 
                    class="btn btn-sm btn-outline-primary"
                    type="button"
                    @click="loadVersionIntoEditor(version.version_number)"
                    :title="$t('cms.page.loadVersion')"
                  >
                    <i class="bi bi-arrow-counterclockwise"> </i>
                  </button>
                </li>
              </ul>
            </div>
          </div>

          <!-- Kommentar für die Version -->
          <div class="mb-3">
            <label for="version-comment" class="form-label">{{ $t('cms.page.comment') }}</label>
            <input
              id="version-comment"
              v-model="model.editComment"
              type="text"
              class="form-control"
              :placeholder="$t('cms.page.commentPlaceholder')"
            />
          </div>

          <!-- Editor Action Buttons -->
          <div class="d-flex gap-2 justify-content-end mt-3">
            <button class="btn btn-secondary" type="button" @click="stopEdit">
              <i class="bi bi-x-circle"> </i>
              {{ $t('cms.page.cancel') }}
            </button>
            <button class="btn btn-warning" type="button" @click="saveAsDraft">
              <i class="bi bi-feather"> </i>
              {{ $t('cms.page.saveAsDraft') }}
            </button>
            <button class="btn btn-success" type="button" @click="saveAndPublish">
              <i class="bi bi-check2-circle"> </i>
              {{ $t('cms.page.saveAndPublish') }}
            </button>
          </div>
        </div>
      </client-only>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {createError} from "h3";
import {useUserStore} from "~/store/UserStore";
import {type FileUploadEvent} from "~/api/TinyMCEHelper";

const userStore = useUserStore();
const {$cmsURL} = useNuxtApp();

interface Props {
  slug: string;
  languages?: string[];
  defaultLanguage?: string;
}

const props = withDefaults(defineProps<Props>(), {
  languages: () => ['de', 'en', 'fr'],
  defaultLanguage: undefined
});

const i18n = useI18n();

// API Response Types based on cms_backend_doc.md
interface PageTranslation {
  language: string;
  title: string;
  content: string;
}

interface PageVersion {
  version_number: number;
  status: 'draft' | 'published' | 'archived';
  comment?: string;
  created_at: string;
  created_by: string;
  translations: PageTranslation[];
}

interface Page {
  id: number;
  slug: string;
  created_at: string;
  updated_at: string;
}

interface EditTranslation {
  title: string;
  content: string;
  isNew: boolean;
}

// Versionsübersicht (ohne Translations für die Liste)
interface VersionSummary {
  version_number: number;
  status: 'draft' | 'published' | 'archived';
  comment?: string;
  created_at: string;
  created_by: string;
}

// Berechtigungen für eine Seite
interface PagePermissions {
  slug_exists: boolean;
  write: boolean;
  read_draft: boolean;
  read_archived: boolean;
  delete: boolean;
}

const model = reactive<{
  authHeader: Record<string, string> | null;
  edit: boolean;
  loading: boolean;
  page: Page | null;
  currentVersion: PageVersion | null;
  activeViewTab: string;
  activeEditTab: string;
  editTranslations: Record<string, EditTranslation> | null;
  editComment: string;
  versions: VersionSummary[];
  loadingVersions: boolean;
  showVersionHistory: boolean;
  pageNotFound: boolean;
  isCreatingPage: boolean;
  permissions: PagePermissions | null;
}>({
  authHeader: null,
  edit: false,
  loading: false,
  page: null,
  currentVersion: null,
  activeViewTab: '',
  activeEditTab: '',
  editTranslations: null,
  editComment: '',
  versions: [],
  loadingVersions: false,
  showVersionHistory: false,
  pageNotFound: false,
  isCreatingPage: false,
  permissions: null
});



// Computed: ordered languages based on props
const orderedLanguages = computed(() => props.languages);

// Check if translation exists for a language
const hasTranslation = (lang: string): boolean => {
  if (!model.currentVersion?.translations) return false;
  return model.currentVersion.translations.some(t => t.language === lang);
};

// Check if translation is new (for edit mode)
const isNewTranslation = (lang: string): boolean => {
  if (!model.editTranslations) return true;
  return model.editTranslations[lang]?.isNew ?? true;
};

// Get translation for active view tab
const activeViewTranslation = computed((): PageTranslation | null => {
  if (!model.currentVersion?.translations) return null;
  return model.currentVersion.translations.find(t => t.language === model.activeViewTab) || null;
});

useSeoMeta({
  title: computed(() => {
    if (model.currentVersion && model.activeViewTab) {
      const translation = model.currentVersion.translations.find(t => t.language === model.activeViewTab);
      return translation ? translation.title : 'CMS Page';
    }
    return '';
  })
});


// Set active view tab
const setActiveViewTab = (lang: string) => {
  if (hasTranslation(lang)) {
    model.activeViewTab = lang;
  }
};

// Set active edit tab
const setActiveEditTab = (lang: string) => {
  model.activeEditTab = lang;
};

// Determine initial active tab based on default language or current locale
const determineInitialTab = (): string => {
  
  const currentLocale = i18n.locale.value;
  
  // Check if current locale has content
  if (hasTranslation(currentLocale)) {
    return currentLocale;
  }

  // If defaultLanguage is set and has content, use it
  if (props.defaultLanguage && hasTranslation(props.defaultLanguage)) {
    return props.defaultLanguage;
  }

  // Find first language with content
  for (const lang of orderedLanguages.value) {
    if (hasTranslation(lang)) {
      return lang;
    }
  }

  // Fallback to first language
  return orderedLanguages.value[0] || 'de';
};

// Update auth header from user store - only if token is valid
const updateAuthHeader = () => {
  // Erst Cleanup durchführen um abgelaufene Sessions zu bereinigen
  userStore.checkAndCleanup();
  
  if (userStore.hasValidToken()) {
    model.authHeader = { Authorization: `Bearer ${userStore.accessToken}` };
  } else {
    model.authHeader = null;
  }
};

// Initialize auth header
updateAuthHeader();

// Asset URL Placeholder für Speicherung
const ASSET_PLACEHOLDER = '$assets$';

// Konvertiert echte Asset-URLs zu Platzhaltern (für Speicherung)
const urlToPlaceholder = (content: string): string => {
  const baseUrl = $cmsURL();
  // Ersetze vollständige Asset-URLs durch Platzhalter
  // Erfasst: {baseUrl}api/cms/v1/assets/{path}
  const assetUrlPattern = new RegExp(
    baseUrl.replace(/[.*+?^${}()|[\]\\]/g, '\\$&') + 'api/cms/v1/assets/',
    'g'
  );
  return content.replace(assetUrlPattern, ASSET_PLACEHOLDER + '/');
};

// Konvertiert Platzhalter zu echten Asset-URLs (für Anzeige)
const placeholderToUrl = (content: string): string => {
  const baseUrl = $cmsURL();
  // Ersetze Platzhalter durch vollständige Asset-URLs
  const placeholderPattern = new RegExp(
    ASSET_PLACEHOLDER.replace(/[.*+?^${}()|[\]\\]/g, '\\$&') + '/',
    'g'
  );
  return content.replace(placeholderPattern, baseUrl + 'api/cms/v1/assets/');
};

// API helper function with automatic retry on 401
const apiRequest = async <T>(
  endpoint: string, 
  options: RequestInit = {},
  withAuth: boolean = true
): Promise<{ data: T | null; error: Error | null }> => {
  try {
    const headers: Record<string, string> = {
      'Content-Type': 'application/json',
      ...(withAuth && model.authHeader ? model.authHeader : {})
    };

    const response = await fetch(`${$cmsURL()}api/cms/v1${endpoint}`, {
      ...options,
      headers: {
        ...headers,
        ...(options.headers as Record<string, string> || {})
      }
    });

    // Bei 401 und wenn Auth-Header gesendet wurde: Token ist ungültig
    // Retry ohne Auth-Header für öffentliche Inhalte
    if (response.status === 401 && withAuth && model.authHeader) {
      console.log('Token expired or invalid, retrying without auth...');
      userStore.logout();
      model.authHeader = null;
      // Retry ohne Auth
      return apiRequest<T>(endpoint, options, false);
    }

    if (!response.ok) {
      throw new Error(`HTTP ${response.status}: ${response.statusText}`);
    }

    const data = await response.json();
    return { data: data as T, error: null };
  } catch (error) {
    return { data: null, error: error as Error };
  }
};

// Load permissions for a slug
const loadPermissions = async (slug: string): Promise<PagePermissions | null> => {
  if (!model.authHeader) return null;
  
  const result = await apiRequest<PagePermissions>(`/pages/_permissions?slug=${encodeURIComponent(slug)}`);
  return result.data || null;
};

// Load page and current version
const loadPage = async (slug: string) => {
  model.loading = true;
  model.pageNotFound = false;
  model.page = null;
  model.currentVersion = null;
  model.permissions = null;
  
  // Lade Permissions wenn eingeloggt
  if (model.authHeader) {
    model.permissions = await loadPermissions(slug);
  }

  // Find page by slug
  const pagesResult = await apiRequest<Page[]>(`/pages?slug=${encodeURIComponent(slug)}`);
  
  if (pagesResult.error || !pagesResult.data?.length) {
    // Seite nicht gefunden - wenn eingeloggt, Option zum Erstellen anbieten
    if (model.authHeader) {
      model.pageNotFound = true;
      model.loading = false;
      return;
    }
    // Nicht eingeloggt - normaler 404 Fehler
    showError(createError({
      statusCode: 404,
      statusMessage: 'Page not found'
    }));
    model.loading = false;
    return;
  }

  model.page = pagesResult.data[0];

  // Load version based on auth status and permissions
  // read_draft erlaubt Zugriff auf aktuelle Version (kann draft sein)
  // Sonst nur published Version laden
  const canReadDraft = model.authHeader && model.permissions?.read_draft;
  const versionEndpoint = canReadDraft
    ? `/pages/${model.page.id}/versions/current`
    : `/pages/${model.page.id}/versions/published`;

  const versionResult = await apiRequest<PageVersion>(versionEndpoint);

  if (versionResult.error || !versionResult.data) {
    showError(createError({
      statusCode: 404,
      statusMessage: 'No published version available'
    }));
    model.loading = false;
    return;
  }

  model.currentVersion = versionResult.data;
  
  // Konvertiere Platzhalter zu echten URLs für die Anzeige
  if (model.currentVersion.translations) {
    model.currentVersion.translations = model.currentVersion.translations.map(t => ({
      ...t,
      content: placeholderToUrl(t.content)
    }));
  }
  
  model.activeViewTab = determineInitialTab();
  model.loading = false;
};

// Create a new page - opens editor only, actual page creation happens on save
const createNewPage = async () => {
  // Token erneuern beim Öffnen des Editors
  await userStore.refreshToken();
  
  // Initialisiere leere Übersetzungen für alle Sprachen
  const editTranslations: Record<string, EditTranslation> = {};
  for (const lang of orderedLanguages.value) {
    editTranslations[lang] = {
      title: '',
      content: '',
      isNew: true
    };
  }
  
  model.editTranslations = editTranslations;
  model.activeEditTab = i18n.locale.value;
  model.pageNotFound = false; // Box ausblenden
  model.edit = true;
  // model.page bleibt null - wird erst beim Speichern erstellt
};

// Restore archived page and start editing
const restoreAndEdit = async () => {
  if (!model.page || !model.currentVersion) return;
  
  // Token erneuern beim Öffnen des Editors
  await userStore.refreshToken();
  
  // Initialisiere Edit-Translations aus der archivierten Version
  const editTranslations: Record<string, EditTranslation> = {};
  
  for (const lang of orderedLanguages.value) {
    const existingTranslation = model.currentVersion.translations?.find(t => t.language === lang);
    if (existingTranslation) {
      editTranslations[lang] = {
        title: existingTranslation.title || '',
        content: existingTranslation.content || '',
        isNew: false
      };
    } else {
      editTranslations[lang] = {
        title: '',
        content: '',
        isNew: true
      };
    }
  }
  
  model.editTranslations = editTranslations;
  model.activeEditTab = i18n.locale.value;
  model.edit = true;
  
  // Lade Versionshistorie
  await loadVersionHistory();
};

// Start edit mode
const startEdit = async () => {
  if (!model.page) return;

  // Token erneuern beim Öffnen des Editors
  await userStore.refreshToken();

  // Initialize edit translations for all languages
  const editTranslations: Record<string, EditTranslation> = {};
  
  for (const lang of orderedLanguages.value) {
    const existingTranslation = model.currentVersion?.translations?.find(t => t.language === lang);
    
    editTranslations[lang] = {
      title: existingTranslation?.title || '',
      content: existingTranslation?.content || '',
      isNew: !existingTranslation
    };
  }

  model.editTranslations = editTranslations;
  model.activeEditTab = i18n.locale.value;
  model.edit = true;
  
  // Lade Versionshistorie
  await loadVersionHistory();
};

// Lade alle Versionen der Seite
const loadVersionHistory = async () => {
  if (!model.page) return;
  
  model.loadingVersions = true;
  const result = await apiRequest<VersionSummary[]>(`/pages/${model.page.id}/versions`);
  
  if (result.data) {
    // Sortiere nach version_number absteigend (neueste zuerst)
    model.versions = result.data.sort((a, b) => b.version_number - a.version_number);
  } else {
    model.versions = [];
  }
  model.loadingVersions = false;
};

// Toggle Versionshistorie-Anzeige
const toggleVersionHistory = () => {
  model.showVersionHistory = !model.showVersionHistory;
};

// Lade eine bestimmte Version in den Editor
const loadVersionIntoEditor = async (versionNumber: number) => {
  if (!model.page || !model.editTranslations) return;
  
  const result = await apiRequest<PageVersion>(`/pages/${model.page.id}/versions/${versionNumber}`);
  
  if (result.error || !result.data) {
    console.error('Failed to load version:', result.error);
    alert(i18n.t('cms.page.loadVersionError'));
    return;
  }
  
  // Konvertiere Platzhalter zu URLs
  const versionData = result.data;
  if (versionData.translations) {
    versionData.translations = versionData.translations.map(t => ({
      ...t,
      content: placeholderToUrl(t.content)
    }));
  }
  
  // Aktualisiere editTranslations mit den Daten der geladenen Version
  for (const lang of orderedLanguages.value) {
    const versionTranslation = versionData.translations?.find(t => t.language === lang);
    
    model.editTranslations[lang] = {
      title: versionTranslation?.title || '',
      content: versionTranslation?.content || '',
      isNew: !versionTranslation
    };
  }
};

// Formatiere Datum für Anzeige
const formatDate = (dateString: string): string => {
  const date = new Date(dateString);
  return date.toLocaleString(i18n.locale.value, {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
};

// Status-Badge CSS-Klasse
const getStatusBadgeClass = (status: string): string => {
  switch (status) {
    case 'published': return 'bg-success';
    case 'draft': return 'bg-warning text-dark';
    case 'archived': return 'bg-secondary';
    default: return 'bg-light text-dark';
  }
};

// Update edit content for a specific language
const updateEditContent = (lang: string, content: string) => {
  if (model.editTranslations) {
    model.editTranslations[lang].content = content;
  }
};

// Stop edit mode
const stopEdit = () => {
  model.edit = false;
  model.editTranslations = null;
  model.editComment = '';
  model.versions = [];
  model.showVersionHistory = false;
};

// Save as draft
const saveAsDraft = async () => {
  await saveVersion('draft');
};

// Save and publish
const saveAndPublish = async () => {
  await saveVersion('published');
};

// Save new version
const saveVersion = async (status: 'draft' | 'published') => {
  if (!model.editTranslations) return;

  // Falls die Seite noch nicht existiert (pageNotFound), erst erstellen
  if (!model.page) {
    const createResult = await apiRequest<Page>('/pages', {
      method: 'POST',
      body: JSON.stringify({
        slug: props.slug
      })
    });
    
    if (createResult.error || !createResult.data) {
      console.error('Failed to create page:', createResult.error);
      alert(i18n.t('cms.page.createError'));
      return;
    }
    
    model.page = createResult.data;
    model.pageNotFound = false;
  }

  // Build translations array from edit data
  const translations: Array<{ language: string; title: string; content: string }> = [];
  
  for (const lang of orderedLanguages.value) {
    const editTrans = model.editTranslations[lang];
    // Only include translations with actual content
    if (editTrans.title || editTrans.content) {
      translations.push({
        language: lang,
        title: editTrans.title,
        // Konvertiere URLs zu Platzhaltern für die Speicherung
        content: urlToPlaceholder(editTrans.content)
      });
    }
  }

  const payload = {
    status,
    comment: model.editComment || (status === 'published' ? 'Published via editor' : 'Saved as draft'),
    translations
  };

  const result = await apiRequest<PageVersion>(
    `/pages/${model.page.id}/versions`,
    {
      method: 'POST',
      body: JSON.stringify(payload)
    }
  );

  if (result.error) {
    console.error('Failed to save version:', result.error);
    alert(i18n.t('cms.page.saveError'));
    return;
  }

  // Reload page data
  await loadPage(props.slug);
  stopEdit();
};

// Archive page (create archived version)
const archivePage = async () => {
  if (!model.page || !model.currentVersion) return;
  
  if (!confirm(i18n.t('cms.page.confirmArchive'))) return;

  const payload = {
    status: 'archived' as const,
    comment: 'Archived via editor',
    // Konvertiere URLs zurück zu Platzhaltern für die Speicherung
    translations: model.currentVersion.translations.map(t => ({
      language: t.language,
      title: t.title,
      content: urlToPlaceholder(t.content)
    }))
  };

  const result = await apiRequest<PageVersion>(
    `/pages/${model.page.id}/versions`,
    {
      method: 'POST',
      body: JSON.stringify(payload)
    }
  );

  if (result.error) {
    console.error('Failed to archive page:', result.error);
    alert(i18n.t('cms.page.archiveError'));
    return;
  }

  // Reload or show 404
  await loadPage(props.slug);
};

// File upload handler
const fileUpload = async (fileUploadRequest: FileUploadEvent) => {
  const blob = fileUploadRequest.blobInfo.blob();
  const filename = fileUploadRequest.blobInfo.filename();
  
  // Generiere einen eindeutigen Pfad für das Asset (z.B. qed/timestamp-filename)
  const timestamp = Date.now();
  const safeName = filename.replace(/[^a-zA-Z0-9.-]/g, '_');
  const assetPath = `qed/${timestamp}-${safeName}`;

  try {
    const response = await fetch(`${$cmsURL()}api/cms/v1/assets/${assetPath}`, {
      method: 'POST',
      headers: {
        ...(model.authHeader || {})
      },
      body: blob
    });

    if (!response.ok) {
      throw new Error(`Upload failed: ${response.status} ${response.statusText}`);
    }

    // Die URL zum Asset ist der Pfad zum API-Endpoint
    const assetUrl = `${$cmsURL()}api/cms/v1/assets/${assetPath}`;
    fileUploadRequest.success(assetUrl);
  } catch (error) {
    console.error('File upload error:', error);
    fileUploadRequest.failure(
      error instanceof Error ? error.message : 'Upload failed', 
      { remove: true }
    );
  }
};

// Watch for locale changes
watch(() => i18n.locale.value, () => {
  if (!model.edit) {
    model.activeViewTab = determineInitialTab();
  }
});

// Watch for slug changes
watch(() => props.slug, async (newSlug) => {
  await loadPage(newSlug);
});

// Initial load
await loadPage(props.slug);

// Token refresh on client
if (process.client) {
  onMounted(() => {
    // Führe bei Client-Hydration ein Cleanup durch
    // SSR könnte mit einem noch gültigen Token gerendert haben, der jetzt abgelaufen ist
    userStore.checkAndCleanup();
    updateAuthHeader();
    
    // Starte Auto-Refresh wenn eingeloggt
    if (userStore.hasValidToken()) {
      userStore.startAutoRefresh();
    }
  });

  onUnmounted(() => {
    userStore.stopAutoRefresh();
  });
  
  // Reagiere auf Token-Änderungen (z.B. nach Refresh)
  watch(() => userStore.accessToken, () => {
    updateAuthHeader();
  });
}
</script>

<style scoped>
.nav-link:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.tab-pane {
  display: none;
}

.tab-pane.active {
  display: block;
}
</style>