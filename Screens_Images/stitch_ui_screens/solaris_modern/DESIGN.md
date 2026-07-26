---
name: Solaris Modern
colors:
  surface: '#fff8f1'
  surface-dim: '#e2d9c8'
  surface-bright: '#fff8f1'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#fdf2e1'
  surface-container: '#f7ebda'
  surface-container-high: '#f1e7d5'
  surface-container-highest: '#ecdfce'
  on-surface: '#1f1b10'
  on-surface-variant: '#4e4632'
  inverse-surface: '#353024'
  inverse-on-surface: '#faf0de'
  outline: '#857868'
  outline-variant: '#d6c9b8'
  surface-tint: '#745b00'
  primary: '#745b00'
  on-primary: '#ffffff'
  primary-container: '#ffcc0f'
  on-primary-container: '#6f5700'
  inverse-primary: '#f1c000'
  secondary: '#675d50'
  on-secondary: '#ffffff'
  secondary-container: '#ecdecd'
  on-secondary-container: '#6b6154'
  tertiary: '#685d4e'
  on-tertiary: '#ffffff'
  tertiary-container: '#e0d0bd'
  on-tertiary-container: '#64584a'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#ffe08c'
  primary-fixed-dim: '#f1c000'
  on-primary-fixed: '#241a00'
  on-primary-fixed-variant: '#584400'
  secondary-fixed: '#eee0d0'
  secondary-fixed-dim: '#d2c4b5'
  on-secondary-fixed: '#211a11'
  on-secondary-fixed-variant: '#4e453a'
  tertiary-fixed: '#f1e0cd'
  tertiary-fixed-dim: '#d4c4b2'
  on-tertiary-fixed: '#221a0f'
  on-tertiary-fixed-variant: '#504537'
  background: '#fff8f1'
  on-background: '#1f1b10'
  surface-variant: '#ebe1d0'
  on-surface-primary: '#1f1a10'
  on-surface-secondary: '#52493d'
  status-work: '#16a34a'
  status-study: '#9333ea'
  status-personal: '#ea580c'
  status-cloud: '#2563eb'
  status-error: '#dc2626'
typography:
  display-lg:
    fontFamily: Inter
    fontSize: 30px
    fontWeight: '700'
    lineHeight: 36px
    letterSpacing: -0.02em
  headline-md:
    fontFamily: Inter
    fontSize: 18px
    fontWeight: '700'
    lineHeight: 24px
  body-md:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  body-sm:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '400'
    lineHeight: 16px
  label-lg:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 20px
  label-md:
    fontFamily: Inter
    fontSize: 11px
    fontWeight: '600'
    lineHeight: 14px
  label-sm:
    fontFamily: Inter
    fontSize: 10px
    fontWeight: '700'
    lineHeight: 12px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  container-padding: 1.25rem
  stack-gap-lg: 2rem
  stack-gap-md: 1.5rem
  stack-gap-sm: 1rem
  grid-gutter: 0.75rem
  element-padding-y: 0.75rem
  element-padding-x: 1rem
---

## Brand & Style
Solaris Modern is a sophisticated, utility-focused productivity aesthetic designed for document management and professional workspaces. It blends the structural reliability of **Corporate Modern** design with a warm, organic color palette that feels more human and approachable than standard enterprise software.

The visual language relies on high-quality typography, subtle tonal layering, and a "soft-contained" approach where elements are grouped in distinct containers with soft borders rather than floating freely. The target audience values organization, speed, and a calm working environment. The overall emotional response should be one of "structured serenity"—providing a sense of order without the coldness of traditional "gray-scale" SaaS interfaces.

## Colors
The palette is rooted in a warm "Solaris" theme, using creamy off-whites and sandy beiges for the background layers instead of pure grays. 

- **Primary:** A vibrant, sun-saturated yellow (#ffcc0f) used for key actions, active states, and focus indicators.
- **Surface Strategy:** The system uses a tiered background approach. The main body utilizes `surface-container-lowest` (#ffffff) for clarity, while `surface` (#fff8f1) and `surface-container` (#f7ebda) provide soft differentiation for headers and interactive regions.
- **Typography Colors:** Primary text uses a deep, warm charcoal (#1f1a10) to maintain high contrast without the harshness of pure black. Secondary text uses a muted brown-gray (#52493d).
- **Semantic Accents:** A secondary palette of soft jewel tones is used sparingly for folder categorization and status badges (e.g., Green for Work, Purple for Study).

## Typography
The system utilizes **Inter** exclusively to ensure a clean, systematic, and highly legible interface across all document types. 

- **Headlines:** Use heavy weights (700) with slight negative letter-spacing to create a strong visual anchor.
- **Body:** Standardized at 14px for optimal readability on mobile devices.
- **Labels:** Meta-information (item counts, timestamps) uses smaller sizes (10px-11px) but maintains legibility through medium or semi-bold weights.
- **Functional Type:** Button labels and navigation items use a 10px bold style to remain distinct even at small sizes.

## Layout & Spacing
The layout follows a **Fluid Grid** model with strict horizontal padding of 20px (`1.25rem`) to frame the content. 

- **Grid System:** A 4-column icon grid is used for folders, while a single-column list view is used for documents.
- **Vertical Rhythm:** Sections are separated by consistent gaps of `1.5rem` to `2rem`.
- **Touch Targets:** Interactive elements like document rows and buttons maintain a minimum height of 44px to accommodate mobile interactions.
- **Adaptation:** On larger screens, the 4-column folder grid scales to 6 or 8 columns, while document lists move to a multi-column "card" layout or a table view with expanded meta-data.

## Elevation & Depth
Elevation is primarily conveyed through **Tonal Layers** and **Low-contrast Outlines** rather than heavy shadows.

- **Surface Levels:** The base is `surface-container-lowest` (#ffffff). Interactive cards and containers use a subtle border (`outline-variant`) to separate themselves from the background.
- **Shadows:** A very soft "shadow-card" (`0 2px 8px rgba(0,0,0,0.04)`) is used on document containers and folder icons to provide a slight lift without appearing heavy.
- **Active States:** Tapping or selecting an item triggers a shift to `surface-container`, providing immediate tactile feedback without needing a change in elevation.
- **Floating Elements:** The scan button in the bottom navigation uses a more pronounced `shadow-md` and a thick 4px border to signify it sits at the highest z-index.

## Shapes
The shape language is consistently **Rounded**, using varied radii to create a hierarchy of containment.

- **Large Containers:** Cards, document lists, and search inputs use a `2xl` (1.5rem) or `xl` (1rem) radius.
- **Small Components:** Filter pills, buttons, and status badges use an `xl` (0.75rem - 1rem) radius.
- **Circular Elements:** Avatars, notification pips, and primary action buttons (like the Scan button) use a full `rounded-full` (9999px) treatment.
- **Document Previews:** Internal file thumbnails use a tighter `rounded-sm` (0.25rem) to maintain a crisp, paper-like appearance.

## Components

- **Buttons:**
  - *Secondary/Icon Buttons:* White background, `outline-variant` border, and shadow-sm. Used for headers and utility actions.
  - *Primary Floating:* Large circular button with `primary` background and high-contrast icon.
- **Chips (Filter Pills):**
  - *Active:* `primary` border, `primary-text`.
  - *Inactive:* `outline-variant` border, `secondary-text`.
  - *Structure:* 8px-12px horizontal padding, 8px vertical padding.
- **Cards (Folders):**
  - Square aspect ratio. 
  - Centered layout: Circular icon container (tinted background) → Title (bold) → Meta-text (smaller, secondary).
- **List Items (Documents):**
  - Horizontal layout: Star toggle → Thumbnail → Content (Title + Meta row) → Actions.
  - Separated by `outline-variant` dividers.
- **Inputs:**
  - Search bars feature a `2xl` radius, subtle inner shadow, and left-aligned icons in `secondary-text`.
- **Progress Indicators:**
  - Circular or linear tracks using `outline-variant` for the background and `primary` for the progress fill.