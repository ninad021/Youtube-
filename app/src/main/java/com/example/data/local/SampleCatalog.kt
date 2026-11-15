package com.example.data.local

import com.example.R
import com.example.data.model.CaptionCue
import com.example.data.model.NotificationUiModel
import com.example.data.model.VideoCategory
import com.example.data.model.VideoItem

object SampleCatalog {

    private const val BASE_SAMPLE_URL = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/"

    val videos: List<VideoItem> = listOf(
        VideoItem(
            id = "vid_tears_of_steel",
            title = "Tears of Steel — 4K Cyberpunk Sci-Fi Short Film",
            description = "Set in a futuristic Amsterdam, a group of warriors and scientists gather at the Oude Kerk to stage a crucial event from the past in a desperate attempt to rescue the world from destructive robots.\n\nProduced by Blender Foundation (Mango Open Movie Project). Streaming via adaptive Media3 ExoPlayer with closed captions and multi-quality selection.",
            thumbnailUrl = "${BASE_SAMPLE_URL}images/TearsOfSteel.jpg",
            fallbackDrawableRes = R.drawable.img_hero_stream,
            videoUrl = "${BASE_SAMPLE_URL}TearsOfSteel.mp4",
            durationSeconds = 734,
            durationLabel = "12:14",
            viewCount = 4_820_400L,
            viewCountFormatted = "4.8M views",
            uploadDateLabel = "2 days ago",
            category = VideoCategory.ENTERTAINMENT,
            channelId = "ch_blender_studio",
            channelName = "Blender Studio",
            channelAvatarColorHex = 0xFFFF1E2D,
            channelHandle = "@BlenderStudio",
            subscriberCount = 2_450_000L,
            subscriberCountFormatted = "2.45M subscribers",
            baseLikes = 184_200,
            isDownloadable = true,
            licenseNote = "Creative Commons Attribution 3.0 — Blender Foundation",
            tags = listOf("sci-fi", "cyberpunk", "4k", "vfx", "short film"),
            captions = listOf(
                CaptionCue(1_000L, 6_000L, "[Ambient futuristic synth humming over Amsterdam skyline]"),
                CaptionCue(6_500L, 12_000L, "Thom: Celia, this is not what we planned forty years ago."),
                CaptionCue(12_500L, 19_000L, "Celia: You never understood what it takes to build something lasting."),
                CaptionCue(20_000L, 28_000L, "[Mechanical footsteps echo across the bridge]")
            )
        ),
        VideoItem(
            id = "vid_big_buck_bunny",
            title = "Big Buck Bunny — Full Remastered Animated Premiere",
            description = "A giant rabbit with a heart bigger than himself finds his peaceful meadow disturbed by three bullying rodents. Follow his clever, hilarious plan in this beloved open-source animated classic.\n\nEncoded with high-efficiency streaming profiles for seamless mobile playback.",
            thumbnailUrl = "${BASE_SAMPLE_URL}images/BigBuckBunny.jpg",
            fallbackDrawableRes = R.drawable.img_hero_stream,
            videoUrl = "${BASE_SAMPLE_URL}BigBuckBunny.mp4",
            durationSeconds = 596,
            durationLabel = "9:56",
            viewCount = 12_300_000L,
            viewCountFormatted = "12.3M views",
            uploadDateLabel = "1 week ago",
            category = VideoCategory.ENTERTAINMENT,
            channelId = "ch_blender_studio",
            channelName = "Blender Studio",
            channelAvatarColorHex = 0xFFFF1E2D,
            channelHandle = "@BlenderStudio",
            subscriberCount = 2_450_000L,
            subscriberCountFormatted = "2.45M subscribers",
            baseLikes = 542_000,
            isDownloadable = true,
            licenseNote = "Creative Commons Attribution 3.0 — Blender Foundation",
            tags = listOf("animation", "3d", "comedy", "family", "cinema"),
            captions = listOf(
                CaptionCue(1_000L, 7_000L, "[Morning birds chirping softly across the sunlit forest]"),
                CaptionCue(7_500L, 14_000L, "[Gentle orchestral strings swell as Bunny steps outside]"),
                CaptionCue(15_000L, 22_000L, "[Fluttering butterfly lands delicately on a clover]")
            )
        ),
        VideoItem(
            id = "vid_sintel",
            title = "Sintel — Epic Dragon Fantasy Short Film (Director's Cut)",
            description = "A lonely wanderer named Sintel searches relentlessly across perilous mountains and frozen wastes for Scales, a baby dragon she befriended and lost.\n\nFeaturing an unforgettable orchestral score and breathtaking character animation.",
            thumbnailUrl = "${BASE_SAMPLE_URL}images/Sintel.jpg",
            fallbackDrawableRes = R.drawable.img_channel_banner,
            videoUrl = "${BASE_SAMPLE_URL}Sintel.mp4",
            durationSeconds = 888,
            durationLabel = "14:48",
            viewCount = 8_910_000L,
            viewCountFormatted = "8.9M views",
            uploadDateLabel = "3 weeks ago",
            category = VideoCategory.ENTERTAINMENT,
            channelId = "ch_open_cinema",
            channelName = "Open Cinema Vault",
            channelAvatarColorHex = 0xFF7C3AED,
            channelHandle = "@OpenCinemaVault",
            subscriberCount = 980_000L,
            subscriberCountFormatted = "980K subscribers",
            baseLikes = 398_100,
            isDownloadable = true,
            licenseNote = "Creative Commons Attribution 3.0 — Durian Open Movie",
            tags = listOf("fantasy", "dragon", "orchestral", "adventure"),
            captions = listOf(
                CaptionCue(1_000L, 6_500L, "[Howling arctic wind sweeps across the mountain pass]"),
                CaptionCue(7_000L, 14_000L, "Shaman: Why do you wander so far from home, traveler?"),
                CaptionCue(14_500L, 21_000L, "Sintel: I'm searching for someone I lost.")
            )
        ),
        VideoItem(
            id = "vid_for_bigger_joy",
            title = "Midnight Synthwave Session — Live Analog Modular Mix",
            description = "Immerse yourself in crisp analog synthesizers, warm tape saturation, and spatial studio sound design recorded live for late-night coding and deep focus.",
            thumbnailUrl = "${BASE_SAMPLE_URL}images/ForBiggerJoy.jpg",
            fallbackDrawableRes = R.drawable.img_channel_banner,
            videoUrl = "${BASE_SAMPLE_URL}ForBiggerJoy.mp4",
            durationSeconds = 15,
            durationLabel = "0:15",
            viewCount = 1_240_000L,
            viewCountFormatted = "1.2M views",
            uploadDateLabel = "5 hours ago",
            category = VideoCategory.MUSIC,
            channelId = "ch_neon_waves",
            channelName = "Neon Waves Audio",
            channelAvatarColorHex = 0xFF06B6D4,
            channelHandle = "@NeonWavesAudio",
            subscriberCount = 615_000L,
            subscriberCountFormatted = "615K subscribers",
            baseLikes = 67_300,
            isDownloadable = true,
            tags = listOf("music", "synthwave", "electronic", "mix", "studio"),
            captions = listOf(
                CaptionCue(500L, 7_000L, "♪ [Warm analog arpeggio pulsing at 112 BPM] ♪"),
                CaptionCue(7_500L, 14_500L, "♪ [Sub bass drops with shimmering reverb pads] ♪")
            )
        ),
        VideoItem(
            id = "vid_elephants_dream",
            title = "Elephants Dream — Experimental Surrealist Soundtrack & Visuals",
            description = "Explore the mechanical labyrinth of Proog and Emo in the world's first open movie project, featuring avant-garde spatial audio and industrial acoustic scoring.",
            thumbnailUrl = "${BASE_SAMPLE_URL}images/ElephantsDream.jpg",
            fallbackDrawableRes = R.drawable.img_channel_banner,
            videoUrl = "${BASE_SAMPLE_URL}ElephantsDream.mp4",
            durationSeconds = 653,
            durationLabel = "10:53",
            viewCount = 3_150_000L,
            viewCountFormatted = "3.1M views",
            uploadDateLabel = "1 month ago",
            category = VideoCategory.MUSIC,
            channelId = "ch_neon_waves",
            channelName = "Neon Waves Audio",
            channelAvatarColorHex = 0xFF06B6D4,
            channelHandle = "@NeonWavesAudio",
            subscriberCount = 615_000L,
            subscriberCountFormatted = "615K subscribers",
            baseLikes = 112_800,
            isDownloadable = true,
            tags = listOf("soundtrack", "experimental", "audio", "3d"),
            captions = listOf(
                CaptionCue(1_000L, 6_000L, "Proog: Look closely at the machine around us."),
                CaptionCue(6_500L, 12_500L, "Emo: Everything here feels alive and shifting.")
            )
        ),
        VideoItem(
            id = "vid_for_bigger_blazes",
            title = "Next-Gen Raytracing Gameplay Showcase — Unreal Engine 5.5",
            description = "Testing real-time global illumination, volumetric fire physics, and 120 FPS mobile frame pacing in an intense cinematic action sequence.",
            thumbnailUrl = "${BASE_SAMPLE_URL}images/ForBiggerBlazes.jpg",
            fallbackDrawableRes = R.drawable.img_hero_stream,
            videoUrl = "${BASE_SAMPLE_URL}ForBiggerBlazes.mp4",
            durationSeconds = 15,
            durationLabel = "0:15",
            viewCount = 2_670_000L,
            viewCountFormatted = "2.6M views",
            uploadDateLabel = "1 day ago",
            category = VideoCategory.GAMING,
            channelId = "ch_pixel_forge",
            channelName = "PixelForge Gaming",
            channelAvatarColorHex = 0xFFF59E0B,
            channelHandle = "@PixelForge",
            subscriberCount = 1_840_000L,
            subscriberCountFormatted = "1.84M subscribers",
            baseLikes = 149_500,
            isDownloadable = true,
            tags = listOf("gaming", "raytracing", "graphics", "showcase"),
            captions = listOf(
                CaptionCue(500L, 7_500L, "[Volumetric ember bursts crackle in real-time 4K]"),
                CaptionCue(8_000L, 14_500L, "Narrator: Notice zero frame drops under peak particle load.")
            )
        ),
        VideoItem(
            id = "vid_for_bigger_meltdowns",
            title = "Pro Esports Finals — Championship Clutch Moments",
            description = "Relive the fastest reaction plays, tactical team rotations, and arena-shaking championship victories from this season's world series.",
            thumbnailUrl = "${BASE_SAMPLE_URL}images/ForBiggerMeltdowns.jpg",
            fallbackDrawableRes = R.drawable.img_hero_stream,
            videoUrl = "${BASE_SAMPLE_URL}ForBiggerMeltdowns.mp4",
            durationSeconds = 15,
            durationLabel = "0:15",
            viewCount = 918_000L,
            viewCountFormatted = "918K views",
            uploadDateLabel = "3 days ago",
            category = VideoCategory.GAMING,
            channelId = "ch_pixel_forge",
            channelName = "PixelForge Gaming",
            channelAvatarColorHex = 0xFFF59E0B,
            channelHandle = "@PixelForge",
            subscriberCount = 1_840_000L,
            subscriberCountFormatted = "1.84M subscribers",
            baseLikes = 54_200,
            isDownloadable = true,
            tags = listOf("esports", "gaming", "highlights", "tournament"),
            captions = listOf(
                CaptionCue(500L, 7_000L, "Caster: What an unbelievable counter-play in the final seconds!"),
                CaptionCue(7_500L, 14_500L, "[Crowd erupts across the arena]")
            )
        ),
        VideoItem(
            id = "vid_what_car_tech",
            title = "Inside Modern Automotive Tech & Budget Engineering Breakdown",
            description = "A deep-dive technical teardown examining powertrain telemetry, onboard diagnostics, suspension geometry, and real-world durability testing.",
            thumbnailUrl = "${BASE_SAMPLE_URL}images/WhatCarCanYouGetForAGrand.jpg",
            fallbackDrawableRes = R.drawable.img_channel_banner,
            videoUrl = "${BASE_SAMPLE_URL}WhatCarCanYouGetForAGrand.mp4",
            durationSeconds = 567,
            durationLabel = "9:27",
            viewCount = 1_950_000L,
            viewCountFormatted = "1.9M views",
            uploadDateLabel = "4 days ago",
            category = VideoCategory.TECHNOLOGY,
            channelId = "ch_circuit_lab",
            channelName = "CircuitLab Tech",
            channelAvatarColorHex = 0xFF3B82F6,
            channelHandle = "@CircuitLab",
            subscriberCount = 1_320_000L,
            subscriberCountFormatted = "1.32M subscribers",
            baseLikes = 89_400,
            isDownloadable = true,
            tags = listOf("technology", "engineering", "review", "hardware"),
            captions = listOf(
                CaptionCue(1_000L, 7_000L, "Host: Today we're inspecting every mechanical sensor and ECU log."),
                CaptionCue(7_500L, 14_000L, "Host: Let's run full thermal and suspension diagnostics.")
            )
        ),
        VideoItem(
            id = "vid_for_bigger_fun",
            title = "Building Ultra-Smooth Mobile Video Pipelines with Media3",
            description = "Architectural walkthrough covering adaptive bitrate streaming, hardware codec selection, low-latency buffering, and edge-to-edge Jetpack Compose UI surfaces.",
            thumbnailUrl = "${BASE_SAMPLE_URL}images/ForBiggerFun.jpg",
            fallbackDrawableRes = R.drawable.img_channel_banner,
            videoUrl = "${BASE_SAMPLE_URL}ForBiggerFun.mp4",
            durationSeconds = 60,
            durationLabel = "1:00",
            viewCount = 462_000L,
            viewCountFormatted = "462K views",
            uploadDateLabel = "6 days ago",
            category = VideoCategory.TECHNOLOGY,
            channelId = "ch_circuit_lab",
            channelName = "CircuitLab Tech",
            channelAvatarColorHex = 0xFF3B82F6,
            channelHandle = "@CircuitLab",
            subscriberCount = 1_320_000L,
            subscriberCountFormatted = "1.32M subscribers",
            baseLikes = 31_200,
            isDownloadable = true,
            tags = listOf("android", "kotlin", "media3", "compose", "tech"),
            captions = listOf(
                CaptionCue(1_000L, 8_000L, "Speaker: Adaptive streaming dynamically adjusts bitrates on the fly."),
                CaptionCue(8_500L, 16_000L, "Speaker: Keeping frame pacing locked at 60Hz ensures zero jank.")
            )
        ),
        VideoItem(
            id = "vid_education_physics",
            title = "How 3D Rendering Engines Simulate Light & Physics",
            description = "Understand ray casting, physically based rendering (PBR), subsurface scattering, and cloth simulation through visual open-movie breakdowns.",
            thumbnailUrl = "${BASE_SAMPLE_URL}images/TearsOfSteel.jpg",
            fallbackDrawableRes = R.drawable.img_hero_stream,
            videoUrl = "${BASE_SAMPLE_URL}TearsOfSteel.mp4",
            durationSeconds = 734,
            durationLabel = "12:14",
            viewCount = 740_000L,
            viewCountFormatted = "740K views",
            uploadDateLabel = "2 weeks ago",
            category = VideoCategory.EDUCATION,
            channelId = "ch_nova_academy",
            channelName = "Nova Academy",
            channelAvatarColorHex = 0xFF10B981,
            channelHandle = "@NovaAcademy",
            subscriberCount = 890_000L,
            subscriberCountFormatted = "890K subscribers",
            baseLikes = 48_900,
            isDownloadable = true,
            tags = listOf("education", "physics", "graphics", "science", "tutorial"),
            captions = listOf(
                CaptionCue(1_000L, 7_000L, "Instructor: Every reflection starts with Fresnel equations."),
                CaptionCue(7_500L, 14_000L, "Instructor: Notice how roughness scatters specular highlights.")
            )
        ),
        VideoItem(
            id = "vid_education_storytelling",
            title = "Visual Storytelling Masterclass — Lighting, Color & Pacing",
            description = "Learn how color grading, silhouette framing, and sound cues guide viewer emotion across dramatic narrative arcs.",
            thumbnailUrl = "${BASE_SAMPLE_URL}images/BigBuckBunny.jpg",
            fallbackDrawableRes = R.drawable.img_channel_banner,
            videoUrl = "${BASE_SAMPLE_URL}BigBuckBunny.mp4",
            durationSeconds = 596,
            durationLabel = "9:56",
            viewCount = 515_000L,
            viewCountFormatted = "515K views",
            uploadDateLabel = "1 month ago",
            category = VideoCategory.EDUCATION,
            channelId = "ch_nova_academy",
            channelName = "Nova Academy",
            channelAvatarColorHex = 0xFF10B981,
            channelHandle = "@NovaAcademy",
            subscriberCount = 890_000L,
            subscriberCountFormatted = "890K subscribers",
            baseLikes = 29_700,
            isDownloadable = true,
            tags = listOf("education", "filmmaking", "storytelling", "design"),
            captions = listOf(
                CaptionCue(1_000L, 7_000L, "Instructor: Warm highlights invite curiosity before conflict arrives.")
            )
        ),
        VideoItem(
            id = "vid_news_global_tech",
            title = "Global Tech Brief — Open Source Media Standards & 8K Streaming",
            description = "Weekly briefing on open media codecs (AV1), next-gen wireless connectivity, and creator ecosystem shifts worldwide.",
            thumbnailUrl = "${BASE_SAMPLE_URL}images/ForBiggerEscapes.jpg",
            fallbackDrawableRes = R.drawable.img_channel_banner,
            videoUrl = "${BASE_SAMPLE_URL}ForBiggerEscapes.mp4",
            durationSeconds = 15,
            durationLabel = "0:15",
            viewCount = 620_000L,
            viewCountFormatted = "620K views",
            uploadDateLabel = "8 hours ago",
            category = VideoCategory.NEWS,
            channelId = "ch_pulse_news",
            channelName = "Pulse World News",
            channelAvatarColorHex = 0xFFEF4444,
            channelHandle = "@PulseWorldNews",
            subscriberCount = 3_100_000L,
            subscriberCountFormatted = "3.1M subscribers",
            baseLikes = 18_400,
            isDownloadable = false,
            licenseNote = "Broadcast Feed — Streaming Only",
            tags = listOf("news", "briefing", "global", "streaming"),
            captions = listOf(
                CaptionCue(500L, 7_500L, "Anchor: Adoption of open-source AV1 hardware decoding doubled this quarter."),
                CaptionCue(8_000L, 14_500L, "Anchor: Viewers now stream 4K HDR at 30% lower bandwidth.")
            )
        ),
        VideoItem(
            id = "vid_news_urban_mobility",
            title = "Future Cities Report — Clean Transit & Smart Infrastructure",
            description = "On-the-ground report examining electrified public transit, autonomous sensor corridors, and sustainable urban design.",
            thumbnailUrl = "${BASE_SAMPLE_URL}images/ForBiggerFun.jpg",
            fallbackDrawableRes = R.drawable.img_hero_stream,
            videoUrl = "${BASE_SAMPLE_URL}ForBiggerFun.mp4",
            durationSeconds = 60,
            durationLabel = "1:00",
            viewCount = 389_000L,
            viewCountFormatted = "389K views",
            uploadDateLabel = "1 day ago",
            category = VideoCategory.NEWS,
            channelId = "ch_pulse_news",
            channelName = "Pulse World News",
            channelAvatarColorHex = 0xFFEF4444,
            channelHandle = "@PulseWorldNews",
            subscriberCount = 3_100_000L,
            subscriberCountFormatted = "3.1M subscribers",
            baseLikes = 12_900,
            isDownloadable = false,
            licenseNote = "Broadcast Feed — Streaming Only",
            tags = listOf("news", "cities", "climate", "mobility"),
            captions = listOf(
                CaptionCue(1_000L, 8_000L, "Reporter: Transit corridors reduced peak congestion by 22%.")
            )
        ),
        VideoItem(
            id = "vid_sports_offroad",
            title = "All-Terrain Rally Challenge — Mountain Dirt & Street Endurance",
            description = "High-speed onboard telemetry and rugged trail action as rally drivers push symmetrical all-wheel-drive chassis across mud, gravel, and tarmac.",
            thumbnailUrl = "${BASE_SAMPLE_URL}images/SubaruOutbackOnStreetAndDirt.jpg",
            fallbackDrawableRes = R.drawable.img_channel_banner,
            videoUrl = "${BASE_SAMPLE_URL}SubaruOutbackOnStreetAndDirt.mp4",
            durationSeconds = 594,
            durationLabel = "9:54",
            viewCount = 2_180_000L,
            viewCountFormatted = "2.1M views",
            uploadDateLabel = "5 days ago",
            category = VideoCategory.SPORTS,
            channelId = "ch_apex_sports",
            channelName = "Apex Motorsports",
            channelAvatarColorHex = 0xFFEA580C,
            channelHandle = "@ApexMotorsports",
            subscriberCount = 1_470_000L,
            subscriberCountFormatted = "1.47M subscribers",
            baseLikes = 94_600,
            isDownloadable = true,
            tags = listOf("sports", "rally", "racing", "offroad", "motorsport"),
            captions = listOf(
                CaptionCue(1_000L, 7_000L, "[Gravel sprays across the rear fender on hairpin turn 3]"),
                CaptionCue(7_500L, 14_000L, "Driver: Grip holds steady entering the uphill ridge.")
            )
        ),
        VideoItem(
            id = "vid_sports_bullrun",
            title = "Cross-Country Bullrun — Grand Touring Sprint Highlights",
            description = "Coast-to-coast endurance highlights featuring dramatic canyon sprints, pit-stop precision, and roaring exhaust notes.",
            thumbnailUrl = "${BASE_SAMPLE_URL}images/WeAreGoingOnBullrun.jpg",
            fallbackDrawableRes = R.drawable.img_hero_stream,
            videoUrl = "${BASE_SAMPLE_URL}WeAreGoingOnBullrun.mp4",
            durationSeconds = 47,
            durationLabel = "0:47",
            viewCount = 1_540_000L,
            viewCountFormatted = "1.5M views",
            uploadDateLabel = "1 week ago",
            category = VideoCategory.SPORTS,
            channelId = "ch_apex_sports",
            channelName = "Apex Motorsports",
            channelAvatarColorHex = 0xFFEA580C,
            channelHandle = "@ApexMotorsports",
            subscriberCount = 1_470_000L,
            subscriberCountFormatted = "1.47M subscribers",
            baseLikes = 72_100,
            isDownloadable = true,
            tags = listOf("sports", "racing", "cars", "endurance"),
            captions = listOf(
                CaptionCue(1_000L, 7_000L, "[Twin-turbocharged engines roar down the canyon straightaway]")
            )
        )
    )

    val initialNotifications: List<NotificationUiModel> = listOf(
        NotificationUiModel(
            id = "notif_1",
            channelName = "Blender Studio",
            title = "New 4K Premiere: Tears of Steel",
            message = "Watch the remastered cyberpunk VFX short film with spatial captions now.",
            timeAgo = "2h ago",
            thumbnailUrl = "${BASE_SAMPLE_URL}images/TearsOfSteel.jpg",
            videoId = "vid_tears_of_steel",
            isRead = false
        ),
        NotificationUiModel(
            id = "notif_2",
            channelName = "Neon Waves Audio",
            title = "Midnight Synthwave Session is live",
            message = "Fresh analog modular mix uploaded for your late-night focus queue.",
            timeAgo = "5h ago",
            thumbnailUrl = "${BASE_SAMPLE_URL}images/ForBiggerJoy.jpg",
            videoId = "vid_for_bigger_joy",
            isRead = false
        ),
        NotificationUiModel(
            id = "notif_3",
            channelName = "PixelForge Gaming",
            title = "Unreal Engine 5.5 Raytracing Showcase",
            message = "See real-time volumetric fire and 120 FPS mobile frame pacing.",
            timeAgo = "1d ago",
            thumbnailUrl = "${BASE_SAMPLE_URL}images/ForBiggerBlazes.jpg",
            videoId = "vid_for_bigger_blazes",
            isRead = false
        ),
        NotificationUiModel(
            id = "notif_4",
            channelName = "Apex Motorsports",
            title = "All-Terrain Rally Challenge Highlights",
            message = "Full 10-minute mountain dirt & street endurance run available.",
            timeAgo = "3d ago",
            thumbnailUrl = "${BASE_SAMPLE_URL}images/SubaruOutbackOnStreetAndDirt.jpg",
            videoId = "vid_sports_offroad",
            isRead = true
        )
    )

    fun getVideoById(id: String): VideoItem? = videos.find { it.id == id }

    fun getRelatedVideos(currentVideo: VideoItem): List<VideoItem> {
        val sameCategory = videos.filter { it.id != currentVideo.id && it.category == currentVideo.category }
        val others = videos.filter { it.id != currentVideo.id && it.category != currentVideo.category }
        return sameCategory + others
    }
}
