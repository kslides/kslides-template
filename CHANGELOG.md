# Change Log

All notable changes to this template will be documented in this file.

## [1.2.5] 6/11/2022

### Updated

#### _build.gradle_

* Update `id 'org.jetbrains.kotlin.jvm' version '1.7.0'` in `plugins{}`

#### _gradle.properties_

* Update `css_version=1.0.0-pre.343`
* Update `kslides_version=0.10.6`
* Update `srcref_version=1.0.8`
* Update `utils_version=1.27.0`

## [1.2.4] 6/5/2022

### Updated

#### _gradle.properties_

* Update `css_version=1.0.0-pre.341`
* Update `kslides_version=0.10.4`
* Update `ktor_version=2.0.2`
* Update `logging_version=2.1.23`
* Update `srcref_version=1.0.7`
* Update `utils_version=1.26.0`

## [1.2.3] 5/25/2022

### Added

Added a `/favicon.ico` file.

### Updated

* Add `docs/favicon.ico`
* Add `src/main/resources/public/favicon.ico` and run `./gradlew clean build`

#### _gradle.properties_

* Update `kslides_version=0.10.3`


## [1.2.2] 5/25/2022

### Added

Added support for including [srcref](https://www.srcref.com) GitHub links.

### Updated

#### _gradle.properties_

* Update `kslides_version=0.10.2`
* Update `css_version=1.0.0-pre.340`
* Update `plotly_version=0.5.2-dev-2`
* Add `srcref_version=1.0.3`

#### _build.gradle_

* Add `implementation "com.github.pambrose:srcref:$srcref_version"` to `dependencies{}`

## [1.2.1] 5/19/2022

### Added

Added support for specifying playground css, which is useful for increasing
the playground font size, as seen [here](https://kslides.com/#/playground).
Playground css values are specified in any _PlaygroundConfig_ `css{}` block
using the CSS DSL or via css strings.

### Updated

#### _gradle.properties_

* Update `kslides_version=0.10.1`
* Update `css_version=1.0.0-pre.338`

## [1.2.0] 5/18/2022

### Added

Added support for plotly-kt ([Example](https://kslides.com/#/plotly))

### Updated

#### _gradle.properties_

* Add `plotly_version=0.5.1-dev-4`

#### _build.gradle_

* Add `maven { url = 'https://repo.kotlin.link' }` to `repositories{}`
* Add `implementation "space.kscience:plotlykt-core:$plotly_version"` to `dependencies{}`
* Update `kslides_version=0.10.0`
* Update `jvmTarget = '11'` in _compileKotlin.kotlinOptions_ and _compileTestKotlin.kotlinOptions_

## [1.1.5] 5/14/2022

### Updated

#### _gradle.properties_

* Update `kslides_version=0.9.0` 