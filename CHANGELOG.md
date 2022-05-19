# Change Log

All notable changes to this template will be documented in this file.

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