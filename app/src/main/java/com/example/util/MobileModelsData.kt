package com.example.util

object MobileModelsData {

    val iPhoneModels = listOf(
        "iPhone 17 Pro Max",
        "iPhone 17 Pro",
        "iPhone 17",
        "iPhone 17 Slim",
        "iPhone 16 Pro Max",
        "iPhone 16 Pro",
        "iPhone 16 Plus",
        "iPhone 16",
        "iPhone 15 Pro Max",
        "iPhone 15 Pro",
        "iPhone 15 Plus",
        "iPhone 15",
        "iPhone 14 Pro Max",
        "iPhone 14 Pro",
        "iPhone 14 Plus",
        "iPhone 14",
        "iPhone 13 Pro Max",
        "iPhone 13 Pro",
        "iPhone 13",
        "iPhone 13 mini",
        "iPhone 12 Pro Max",
        "iPhone 12 Pro",
        "iPhone 12",
        "iPhone 12 mini",
        "iPhone 11 Pro Max",
        "iPhone 11 Pro",
        "iPhone 11",
        "iPhone XS Max",
        "iPhone XS",
        "iPhone XR",
        "iPhone X",
        "iPhone SE (3rd Gen)",
        "iPhone SE (2nd Gen)",
        "iPhone 8 Plus",
        "iPhone 8",
        "iPhone 7 Plus",
        "iPhone 7"
    )

    fun getIPhoneColors(model: String): List<String> {
        val lower = model.lowercase()
        return when {
            lower.contains("16 pro") -> listOf("Desert Titanium", "Natural Titanium", "White Titanium", "Black Titanium")
            lower.contains("16") -> listOf("Ultramarine", "Teal", "Pink", "White", "Black")
            lower.contains("15 pro") -> listOf("Natural Titanium", "Blue Titanium", "White Titanium", "Black Titanium")
            lower.contains("15") -> listOf("Pink", "Yellow", "Green", "Blue", "Black")
            lower.contains("14 pro") -> listOf("Deep Purple", "Space Black", "Gold", "Silver")
            lower.contains("14") -> listOf("Midnight", "Starlight", "Product(RED)", "Blue", "Purple", "Yellow")
            lower.contains("13 pro") -> listOf("Alpine Green", "Sierra Blue", "Graphite", "Gold", "Silver")
            lower.contains("13") -> listOf("Green", "Pink", "Blue", "Midnight", "Starlight", "Product(RED)")
            lower.contains("12 pro") -> listOf("Pacific Blue", "Graphite", "Gold", "Silver")
            lower.contains("12") -> listOf("Purple", "Blue", "Green", "Product(RED)", "White", "Black")
            lower.contains("11 pro") -> listOf("Midnight Green", "Space Gray", "Silver", "Gold")
            lower.contains("11") -> listOf("Purple", "Green", "Yellow", "Black", "White", "Product(RED)")
            else -> listOf(
                "Natural Titanium",
                "Space Black",
                "Deep Purple",
                "Alpine Green",
                "Sierra Blue",
                "Pacific Blue",
                "Graphite",
                "Gold",
                "Silver",
                "Midnight",
                "Starlight",
                "Product(RED)"
            )
        }
    }

    val androidBrands = listOf(
        "Samsung",
        "Google Pixel",
        "Xiaomi / Redmi",
        "OnePlus",
        "Vivo",
        "Oppo",
        "Realme",
        "Infinix",
        "Tecno",
        "Motorola",
        "Nothing"
    )

    val androidModelsByBrand = mapOf(
        "Samsung" to listOf(
            "Galaxy S25 Ultra",
            "Galaxy S25+",
            "Galaxy S25",
            "Galaxy S24 Ultra",
            "Galaxy S24+",
            "Galaxy S24",
            "Galaxy S23 Ultra",
            "Galaxy S23 FE",
            "Galaxy S23",
            "Galaxy Z Fold 6",
            "Galaxy Z Flip 6",
            "Galaxy Z Fold 5",
            "Galaxy Z Flip 5",
            "Galaxy A55 5G",
            "Galaxy A35 5G",
            "Galaxy A25 5G",
            "Galaxy A15",
            "Galaxy A05s",
            "Galaxy Note 20 Ultra"
        ),
        "Google Pixel" to listOf(
            "Pixel 9 Pro Fold",
            "Pixel 9 Pro XL",
            "Pixel 9 Pro",
            "Pixel 9",
            "Pixel 8 Pro",
            "Pixel 8",
            "Pixel 8a",
            "Pixel 7 Pro",
            "Pixel 7",
            "Pixel 7a",
            "Pixel 6 Pro",
            "Pixel 6"
        ),
        "Xiaomi / Redmi" to listOf(
            "Xiaomi 14 Ultra",
            "Xiaomi 14",
            "Xiaomi 13T Pro",
            "Redmi Note 14 Pro+",
            "Redmi Note 13 Pro+",
            "Redmi Note 13 Pro",
            "Redmi Note 13",
            "Redmi 13C",
            "Redmi 12",
            "POCO F6 Pro",
            "POCO X6 Pro",
            "POCO M6 Pro"
        ),
        "OnePlus" to listOf(
            "OnePlus 13",
            "OnePlus 12",
            "OnePlus 12R",
            "OnePlus Open",
            "OnePlus 11",
            "OnePlus Nord 4",
            "OnePlus Nord CE4",
            "OnePlus Nord CE3 Lite"
        ),
        "Vivo" to listOf(
            "Vivo X100 Pro",
            "Vivo X90 Pro",
            "Vivo V30 Pro",
            "Vivo V30",
            "Vivo V29 5G",
            "Vivo Y200",
            "Vivo Y28",
            "Vivo Y17s"
        ),
        "Oppo" to listOf(
            "Oppo Find X7 Ultra",
            "Oppo Reno 12 Pro",
            "Oppo Reno 12",
            "Oppo Reno 11 Pro",
            "Oppo Reno 11",
            "Oppo A79 5G",
            "Oppo A58",
            "Oppo A18"
        ),
        "Realme" to listOf(
            "Realme GT 6",
            "Realme 12 Pro+ 5G",
            "Realme 12+ 5G",
            "Realme 11 Pro 5G",
            "Realme C67",
            "Realme C55",
            "Realme C53"
        ),
        "Infinix" to listOf(
            "Infinix Zero 30 5G",
            "Infinix Note 40 Pro",
            "Infinix Note 40",
            "Infinix Note 30 Pro",
            "Infinix Hot 40 Pro",
            "Infinix Hot 30",
            "Infinix Smart 8"
        ),
        "Tecno" to listOf(
            "Tecno Camon 30 Premier",
            "Tecno Camon 30 Pro",
            "Tecno Camon 20 Pro",
            "Tecno Spark 20 Pro+",
            "Tecno Spark 20",
            "Tecno Pova 6 Pro"
        ),
        "Motorola" to listOf(
            "Edge 50 Ultra",
            "Edge 50 Pro",
            "Razr 50 Ultra",
            "Moto G84 5G",
            "Moto G54 5G"
        ),
        "Nothing" to listOf(
            "Nothing Phone (2)",
            "Nothing Phone (2a)",
            "Nothing Phone (1)"
        )
    )

    val androidColors = listOf(
        "Phantom Black",
        "Titanium Gray",
        "Cream",
        "Amber Yellow",
        "Cobalt Violet",
        "Marble Gray",
        "Mint Green",
        "Obsidian",
        "Porcelain",
        "Bay Blue",
        "Hazel",
        "Rose",
        "Sunset Orange",
        "Aurora",
        "Ice Blue",
        "Silver Frost",
        "Midnight Black",
        "Emerald Green"
    )

    val storageOptions = listOf("64 GB", "128 GB", "256 GB", "512 GB", "1 TB")
    val ramOptions = listOf("4 GB", "6 GB", "8 GB", "12 GB", "16 GB", "N/A")

    val ptaOptions = listOf(
        "PTA Approved",
        "Non-PTA",
        "Non-PTA SIM Working",
        "JV (Gevey)",
        "Software Bypass"
    )

    val conditionOptions = listOf(
        "Brand New / Box Packed",
        "10/10 Like New / Pinpack",
        "9.5/10 Mint Condition",
        "9/10 Minor Scratches",
        "Fair Condition",
        "Refurbished"
    )

    val expenseCategories = listOf(
        "Shop Rent",
        "Electricity & Utilities",
        "Staff Salary",
        "Tea & Refreshments",
        "Mobile Spare Parts & Repair",
        "Shop Maintenance",
        "Packaging & Boxes",
        "Transport / Delivery",
        "Miscellaneous"
    )
}
