# PowerShell script: Create 10 draft posts and publish 5 of them

# Array to store post IDs
$post_ids = @()

Write-Host "Starting script..."

# Check server status
try {
    Write-Host "Checking server status..."
    $testResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/posts/published" -Method Get -ErrorAction Stop
    Write-Host "Server is running normally."
} catch {
    Write-Host "Error: Cannot connect to server. Please ensure Spring Boot application is running."
    Write-Host "Error details: $($_.Exception.Message)"
    exit 1
}

# Create 10 draft posts
for ($i = 1; $i -le 10; $i++) {
    try {
        Write-Host "Creating draft post $i..."
        $jsonBody = @{
            content = "Post $i content. This is a draft post."
        } | ConvertTo-Json
        
        $response = Invoke-RestMethod -Uri "http://localhost:8080/api/posts/drafts" -Method Post -ContentType "application/json" -Body $jsonBody -ErrorAction Stop
        $post_ids += $response.id
        Write-Host "Draft post $i created successfully. ID: $($response.id)"
        
        # Wait to create time difference
        Start-Sleep -Seconds 1
    } catch {
        Write-Host "Error: Failed to create draft post $i"
        Write-Host "Error details: $($_.Exception.Message)"
        exit 1
    }
}

Write-Host "10 draft posts have been created."
Write-Host "-------------------------------------"

# Publish first 5 posts
for ($i = 0; $i -lt 5; $i++) {
    try {
        $id = $post_ids[$i]
        Write-Host "Publishing post ID: $id..."
        $response = Invoke-RestMethod -Uri "http://localhost:8080/api/posts/drafts/$id/publish" -Method Put -ErrorAction Stop
        Write-Host "Post ID: $id has been published."
        
        # Wait to create time difference
        Start-Sleep -Seconds 1
    } catch {
        Write-Host "Error: Failed to publish post ID: $($post_ids[$i])"
        Write-Host "Error details: $($_.Exception.Message)"
    }
}

Write-Host "-------------------------------------"
Write-Host "5 posts have been published."
Write-Host "Remaining 5 posts are still in draft status."

# Check published posts
try {
    Write-Host "-------------------------------------"
    Write-Host "Published posts list:"
    $publishedPosts = Invoke-RestMethod -Uri "http://localhost:8080/api/posts/published" -Method Get -ErrorAction Stop
    if ($publishedPosts.Count -eq 0) {
        Write-Host "No published posts found."
    } else {
        $publishedPosts | ConvertTo-Json -Depth 3
    }
} catch {
    Write-Host "Error: Failed to retrieve published posts."
    Write-Host "Error details: $($_.Exception.Message)"
}

# Check draft posts
try {
    Write-Host "-------------------------------------"
    Write-Host "Draft posts list:"
    $draftPosts = Invoke-RestMethod -Uri "http://localhost:8080/api/posts/drafts" -Method Get -ErrorAction Stop
    if ($draftPosts.Count -eq 0) {
        Write-Host "No draft posts found."
    } else {
        $draftPosts | ConvertTo-Json -Depth 3
    }
} catch {
    Write-Host "Error: Failed to retrieve draft posts."
    Write-Host "Error details: $($_.Exception.Message)"
}

Write-Host "-------------------------------------"
Write-Host "Script completed successfully."
